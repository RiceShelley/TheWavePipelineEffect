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

import spinal.core._
import spinal.core.sim._

case class UartDriver(baudrate: HertzNumber, uartTx: Bool, uartRx: Bool) {

  val period = timeToLong(baudrate.toTime)
  uartRx #= true

  def readByte(sync: Boolean): Int = {

    waitUntil(uartTx.toBoolean == false)
    sleep(period / 2)
    assert(uartTx.toBoolean == false)

    sleep(period)

    val byte = Seq
      .fill[Int](8) {
        val bit = uartTx.toBoolean.toInt
        sleep(period)
        bit
      }
      .reverse
      .foldLeft(0) { case (num, bit) =>
        (num << 1) | bit
      }
    // Is stop bit set?
    assert(uartTx.toBoolean == true)
    byte
  }

  def writeByte(byte: Int): Unit = {
    uartRx #= false
    sleep(period)

    for (bitId <- 0 until 8) {
      uartRx #= ((byte >> bitId) & 1) != 0
      sleep(period)
    }

    uartRx #= true
    sleep(period)
  }

  def read(size: Int): Seq[Int] = {
    waitUntil(uartTx.toBoolean == true)

    Seq.tabulate[Int](size) { i =>
      readByte(sync = (i == 0))
    }
  }

  def write(bytes: Int*): Unit = {
    bytes.foreach { byte =>
      writeByte(byte)
    }
  }

}
