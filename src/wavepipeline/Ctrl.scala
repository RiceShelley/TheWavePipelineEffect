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
import spinal.lib._

import spinal.lib.bus.amba4.axilite._
import spinal.lib.bus.misc.SizeMapping

import spinal.lib.bus.regif.AxiLite4BusInterface
import spinal.lib.bus.regif.AccessType
import spinal.lib.bus.regif._

case class Ctrl(axi4LiteConfig: AxiLite4Config, wpDataWidth: Int = 8)
    extends Component {

  val io = new Bundle {

    ////////////////////////////////////////////////////////
    // Axi4 lite bus
    ////////////////////////////////////////////////////////
    val bus = slave(AxiLite4(axi4LiteConfig))

    ////////////////////////////////////////////////////////
    // Wave pipeline control signals
    ////////////////////////////////////////////////////////
    val startTest = out Bool ()

    val dataIn = master(
      Stream(
        new Bundle {
          val a = Bits(wpDataWidth bits)
          val b = Bits(wpDataWidth bits)
        }
      )
    )

    val dataOut = slave(Stream(Bits(wpDataWidth * 2 bits)))
    val outputOccupancy = in UInt (16 bits)
  }

  val busif = AxiLite4BusInterface(
    bus = io.bus,
    sizeMap = SizeMapping(0, 100 Byte)
  )

  //////////////////////////////////////////////////////////
  // ID Register
  //////////////////////////////////////////////////////////
  val idReg = busif.newReg(doc = "ID_REG")
  val idRegField =
    idReg.field(Bits(32 bits), AccessType.RO, doc = "ID Register")
  idRegField := 0xcafebeefL

  //////////////////////////////////////////////////////////
  // Start test register
  //////////////////////////////////////////////////////////
  val startTestReg = busif.newReg(doc = "START_TEST")
  val startTestField = startTestReg.field(
    Bool(),
    AccessType.W1P,
    doc = "Write 1 to start wave pipeline test"
  )
  io.startTest := startTestField

  //////////////////////////////////////////////////////////
  // Register for loading operands into test FIFO
  //////////////////////////////////////////////////////////
  val loadOpsReg = busif.newReg(doc = "LOAD_OPERANDS")
  val opA = loadOpsReg.field(Bits(wpDataWidth bits), AccessType.WO, "Operand A")
  val opB = loadOpsReg.field(Bits(wpDataWidth bits), AccessType.WO, "Operand B")

  io.dataIn.valid := RegNext(loadOpsReg.hitDoWrite, init = False)
  io.dataIn.a := opA.resized
  io.dataIn.b := opB.resized

  //////////////////////////////////////////////////////////
  // Register holds the occupancy of test results FIFO
  //////////////////////////////////////////////////////////
  val readResultsOccupReg = busif.newReg(doc = "RESULT_FIFO_OCCUPANCY")
  val occupancy = readResultsOccupReg.field(
    Bits(32 bits),
    AccessType.RO,
    "Read occupancy of test result FIFO"
  )
  occupancy := io.outputOccupancy.asBits.resized

  //////////////////////////////////////////////////////////
  // Register for reading results out of test FIFO
  //////////////////////////////////////////////////////////
  val readResultsReg = busif.newReg(doc = "RESULTS")
  val results = readResultsReg.field(
    Bits(32 bits),
    AccessType.RO,
    "Read results from test output fifo"
  )
  results := io.dataOut.payload.resized
  io.dataOut.ready := readResultsReg.hitDoRead

  busif.accept(HtmlGenerator("regbank", "wave pipeline"))

}
