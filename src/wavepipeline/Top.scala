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

import spinal.lib.fsm._
import spinal.lib.bus.amba4.axilite.AxiLite4Config
import spinal.lib.com.uart.Uart

import wavepipeline.comms._

// Hardware definition
case class Top(
    sim: Boolean,
    dataWidth: Int,
    pipelineDelayStages: Int,
    testDataBufferDepth: Int = 512
) extends Component {

  val axi4LiteConfig = AxiLite4Config(
    dataWidth = 32,
    addressWidth = 32
  )

  val io = new Bundle {
    val uart = master(Uart(ctsGen = false, rtsGen = false))

    val wavepipeClk = in Bool ()
    val wavepipeRst = in Bool ()
  }

  val axi4LiteMaster = UartToAxiLite4(
    baudrate = 3 MHz,
    axi4LiteConfig = axi4LiteConfig,
    uartPort = io.uart
  )

  val ctrl = Ctrl(axi4LiteConfig)
  ctrl.io.bus <> axi4LiteMaster

  //////////////////////////////////////////////////////////
  // Create clocking area for wave pipeline
  //////////////////////////////////////////////////////////
  val wavepipeClkDom = ClockDomain(io.wavepipeClk, io.wavepipeRst)

  // Sync input data to wave pipeline clk domain
  val wPipeInputData = ctrl.io.dataIn.queue(
    size = 16,
    pushClock = ClockDomain.current,
    popClock = wavepipeClkDom
  )

  val wavepipeClkArea = new ClockingArea(wavepipeClkDom) {

    //////////////////////////////////////////////////////////
    // Logic to drive input stream into wave pipeline
    //////////////////////////////////////////////////////////
    val (testDataBuffer, occupancy) =
      wPipeInputData.queueWithOccupancy(testDataBufferDepth)

    val testDataStream = cloneOf(testDataBuffer)

    val testFsm = new StateMachine {
      val idle: State = new State with EntryPoint
      val sendSamples: State = new State

      val startTestSynced = BufferCC(ctrl.io.startTest)

      idle.whenIsActive {
        when(startTestSynced.rise()) {
          goto(sendSamples)
        }
      }
      sendSamples.whenIsActive {
        when(occupancy === 0) {
          goto(idle)
        }
      }

      testDataStream << testDataBuffer.continueWhen(isActive(sendSamples))

    }

    val wPipe = WavepipeWrapper(
      sim = sim,
      dataWidth = dataWidth,
      pipelineDelayStages = pipelineDelayStages
    )
    // Connect test data to wave pipeline input
    val tDataFlowR = testDataStream.stage().toFlow
    wPipe.io.input.valid := tDataFlowR.valid
    wPipe.io.input.payload.a := tDataFlowR.payload.a
    wPipe.io.input.payload.b := tDataFlowR.payload.b

    // Buffer output data
    val outputBuff = wPipe.io.output.toStream.queue(testDataBufferDepth)
  }

  val outputDataStream = wavepipeClkArea.outputBuff
    .queue(16, pushClock = wavepipeClkDom, popClock = ClockDomain.current)

  val (outputDataBuff, outputOccupancy) =
    outputDataStream.queueWithOccupancy(testDataBufferDepth)

  ctrl.io.dataOut << outputDataBuff
  ctrl.io.outputOccupancy := outputOccupancy.resized
}
