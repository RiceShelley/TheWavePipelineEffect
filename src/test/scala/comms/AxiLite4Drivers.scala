/*

The MIT License (MIT)

Copyright (c) 2024-present Rice Shelley

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package wavepipeline.comms

import spinal.core.sim._
import com.fazecast.jSerialComm.SerialPort

trait AxiLite4Driver {
  val stride: Int

  def write(address: BigInt, data: BigInt): Unit
  def read(address: BigInt): BigInt

  def writeMany(address: BigInt, data: Seq[BigInt], incAddr: Boolean): Unit = {
    data.zipWithIndex.foreach { case (data, idx) =>
      val addrInc = if (incAddr) (idx * stride) else 0
      write(address = address + addrInc, data = data)
    }
  }

  def readMany(address: BigInt, len: Int, incAddr: Boolean): Seq[BigInt] = {
    Seq.tabulate[BigInt](len) { idx =>
      val addrInc = if (incAddr) (idx * stride) else 0
      read(address = address)
    }
  }

  def buildWrPkt(address: BigInt, data: BigInt): Seq[Int] = {
    val addressAsBytes = Seq.tabulate[Int](4) { i =>
      ((address >> (i * 8)) & 0xff).toInt
    }
    val dataAsBytes = Seq.tabulate[Int](4) { i =>
      ((data >> (i * 8)) & 0xff).toInt
    }
    addressAsBytes ++ dataAsBytes :+ 0x01
  }

  def buildRdPkt(address: BigInt): Seq[Int] = {
    val addressAsBytes = Seq.tabulate[Int](4) { i =>
      ((address >> (i * 8)) & 0xff).toInt
    }
    val dataAsBytes = Seq.fill[Int](4) { 0 }
    addressAsBytes ++ dataAsBytes :+ 0x00
  }
}

case class AxiLite4SimUartDriver(uartDriver: UartDriver)
    extends AxiLite4Driver {

  val stride = 4

  def write(address: BigInt, data: BigInt): Unit = {
    uartDriver.write(buildWrPkt(address, data): _*)
  }

  def read(address: BigInt): BigInt = {
    // Write rdPkt to UART port
    val wrThread = fork {
      uartDriver.write(buildRdPkt(address): _*)
    }

    // Read response from UART port
    val resp =
      uartDriver.read(4).reverse.foldLeft(BigInt(0)) { case (acc, byte) =>
        (acc << 8) | byte
      }
    // Write thread should have finished by now
    assert(wrThread.isDone)

    // Return value read
    resp
  }
}

case class AxiLite4HwUartDriver(uartDriver: SerialPort) extends AxiLite4Driver {

  val stride = 4

  def write(address: BigInt, data: BigInt): Unit = {
    val pkt = buildWrPkt(address, data).map { _.toByte }.toArray
    uartDriver.writeBytes(pkt, pkt.length)
  }

  def read(address: BigInt): BigInt = {
    val pkt = buildRdPkt(address).map { _.toByte }.toArray
    uartDriver.writeBytes(pkt, pkt.length)
    // Read response
    Seq
      .tabulate[Int](4) { _ =>
        var buffer = Array[Byte](0)
        while (uartDriver.readBytes(buffer, 1) == 0) {}
        (buffer(0).toInt & 0xff)
      }
      .reverse
      .foldLeft(BigInt(0)) { case (acc, byte) =>
        (acc << 8) | byte
      }
  }
}
