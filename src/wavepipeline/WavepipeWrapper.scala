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
import wavepipeline.mult.MultBlock

class Wavepipe(dataWidth: Int) extends BlackBox {
  val io = new Bundle {
    val clk = in Bool ()
    val divisor0 = in Bits (dataWidth bits)
    val divisor1 = in Bits (dataWidth bits)
    val dIn = in Bits (dataWidth bits)
    val dOut = out Bits (dataWidth bits)
  }

  mapClockDomain(clock = io.clk)

}

case class WavepipeWrapper(
    dataWidth: Int,
    pipelineDelayStages: Int,
    sim: Boolean
) extends Component {

  val io = new Bundle {
    val input = slave(
      Flow(
        new Bundle {
          val a = Bits(dataWidth bits)
          val b = Bits(dataWidth bits)
        }
      )
    )
    val output = master(Flow(Bits(dataWidth * 2 bits)))
  }

  val retimingStages = 3;

  io.output.valid := Delay(
    Delay(io.input.valid, pipelineDelayStages),
    retimingStages * 2
  )

  val mult = MultBlock(sim, dataWidth = dataWidth)
  mult.io.xInput := Delay(io.input.payload.a, retimingStages)
  mult.io.yInput := Delay(io.input.payload.b, retimingStages)
  io.output.payload := Delay(mult.io.output, retimingStages)

}
