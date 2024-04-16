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

package wavepipeline

import spinal.core._

import spinal.core.sim.SimCompiled

import wavepipeline.comms._

import com.fazecast.jSerialComm.SerialPort

object WavepipeHwTest extends App {

  val compiled: SimCompiled[Top] = Config.sim
    .withConfig(
      Config.spinal.copy(
        defaultClockDomainFrequency = FixedFrequency(100 MHz),
        defaultConfigForClockDomains = ClockDomainConfig(
          resetKind = SYNC,
          resetActiveLevel = LOW
        )
      )
    )
    .compile(
      Top(sim = true, dataWidth = 8, pipelineDelayStages = 1)
    )

  //////////////////////////////////////////////////////////
  // List all serial ports
  //////////////////////////////////////////////////////////
  val ports = SerialPort.getCommPorts
  println("Serial ports:")
  ports.zipWithIndex.foreach({ case (port, i) =>
    println(f"${i}: ${port}")
  })

  //////////////////////////////////////////////////////////
  // Connect to serial port
  //////////////////////////////////////////////////////////
  val port = ports(0)
  port.openPort()
  port.setBaudRate(3000000)

  val busDriver = AxiLite4HwUartDriver(uartDriver = port)

  // Read ID register
  println(
    f"idReg = 0x${busDriver.read(Addresses.idReg).toInt.toHexString}"
  )

  //////////////////////////////////////////////////////////
  // Run test
  //////////////////////////////////////////////////////////
  def getOccupancy(): Int = {
    busDriver.read(address = Addresses.resultsOccupReg).toInt
  }

  def testDataFmt(data: Seq[(Int, Int)]): Seq[BigInt] = {
    data.map { case (a, b) =>
      val dWidth = compiled.dut.dataWidth
      val mask = (1 << dWidth) - 1
      (BigInt(b & mask) << dWidth) | BigInt(a & mask)
    }
  }

  def randValue() =
    valueRange(scala.util.Random.nextInt(valueRange.length))

  def toSigned(v: Int, dWidth: Int): Int = {
    val mask = (1 << dWidth) - 1
    val isNeg = ((v >> (dWidth - 1)) & 1) == 1
    if (isNeg) {
      -1 * ((~v & mask) + 1)
    } else {
      v & mask
    }
  }

  val minValue = (1 << (compiled.dut.dataWidth - 1)) * -1
  val maxValue = (1 << (compiled.dut.dataWidth - 1)) - 1
  val valueRange = (minValue to maxValue).toList

  val trials = 100
  val testsResults = (0 until trials)
    .map { idx =>
      // Generate test data
      val testDataLen = 100
      val testData: List[(Int, Int)] = (0 until testDataLen).map { n =>
        (randValue(), randValue())
      }.toList

      val expected = testData.map { case (a, b) => a * b }

      // Load test data into design
      busDriver.writeMany(
        address = Addresses.loadOpsReg,
        data = testDataFmt(testData),
        incAddr = false
      )

      // Start pipeline
      busDriver.write(address = Addresses.startTestReg, data = 1)

      // Wait for test completion
      while (getOccupancy() != testData.length) {}

      // Read data back out
      val outputTestData = busDriver
        .readMany(
          address = Addresses.readResults,
          len = testData.length,
          incAddr = false
        )
        .map { n =>
          toSigned(n.toInt, dWidth = compiled.dut.dataWidth * 2)
        }
        .toList

      println(f"expected ${expected}")
      println(f"got output data ${outputTestData}")

      println(f"test passed ${expected == outputTestData}")
      expected == outputTestData
    }

  val allTestsPassed = testsResults.reduce(_ && _)
  val testsPassed = testsResults.foldLeft(0) { case (acc, t) =>
    if (t == true) acc + 1 else acc
  }
  val testsFailed = testsResults.foldLeft(0) { case (acc, t) =>
    if (t == false) acc + 1 else acc
  }

  println(f"tests passed ${testsPassed}")
  println(f"tests failed ${testsFailed}")
  println(f"all tests passed ${allTestsPassed}")

  port.closePort()
}
