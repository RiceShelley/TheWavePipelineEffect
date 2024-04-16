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
import spinal.lib._

import spinal.lib.bus.amba4.axilite._
import spinal.lib.fsm._

case class StreamToAxiLite4Cmd(config: AxiLite4Config) extends Bundle {
  val address = UInt(config.addressWidth bits)
  val data = Bits(config.dataWidth bits)
  val isWr = Bool()
}

case class StreamToAxiLite4Resp(config: AxiLite4Config) extends Bundle {
  val data = Bits(config.dataWidth bits)
}

case class StreamToAxiLite4(config: AxiLite4Config) extends Component {

  val io = new Bundle {
    val cmd = slave(Stream(StreamToAxiLite4Cmd(config)))
    val resp = master(Stream(StreamToAxiLite4Resp(config)))

    val axiLite = master(AxiLite4(config = config))
  }

  // Register incoming command when stream fires
  val cmdR = RegNextWhen(io.cmd.payload, io.cmd.fire)

  // Capture wr response channel
  val wrRespR = RegNextWhen(io.axiLite.b.resp, io.axiLite.b.fire)

  //////////////////////////////////////////////////////////
  // AXI4 Lite signal defaults
  //////////////////////////////////////////////////////////
  // Address write channel defaults
  io.axiLite.aw.valid := False
  io.axiLite.aw.addr := cmdR.address

  // Write channel defaults
  io.axiLite.w.valid := False
  io.axiLite.w.data := cmdR.data
  io.axiLite.w.strb.setAll()

  // Write channel response defaults
  io.axiLite.b.ready := False

  // Read address channel defaults
  io.axiLite.ar.valid := False
  io.axiLite.ar.addr := cmdR.address

  // Read channel defaults
  io.axiLite.r.ready := False

  val respStream = cloneOf(io.resp)
  respStream.data := io.axiLite.r.data
  respStream.valid := False
  io.resp <-< respStream

  val fsm = new StateMachine() {
    val idle: State = new State with EntryPoint
    val doAw: State = new State
    val doWrite: State = new State
    val getWResp: State = new State
    val doAr: State = new State
    val doR: State = new State

    io.cmd.ready := False

    idle.whenIsActive {
      io.cmd.ready := True
      when(io.cmd.fire) {
        when(io.cmd.payload.isWr) {
          goto(doAw)
        } otherwise {
          goto(doAr)
        }
      }
    }
    ////////////////////////////////////////////////////////
    // Handle AXI4 Lite Write
    ////////////////////////////////////////////////////////
    doAw.whenIsActive {
      io.axiLite.aw.valid := True
      when(io.axiLite.aw.fire) {
        goto(doWrite)
      }
    }
    doWrite.whenIsActive {
      io.axiLite.w.valid := True
      when(io.axiLite.w.fire) {
        goto(getWResp)
      }
    }
    getWResp.whenIsActive {
      io.axiLite.b.ready := True
      when(io.axiLite.b.fire) {
        goto(idle)
      }
    }
    ////////////////////////////////////////////////////////
    // Handle AXI4 Lite Read
    ////////////////////////////////////////////////////////
    doAr.whenIsActive {
      io.axiLite.ar.valid := True
      when(io.axiLite.ar.fire) {
        goto(doR)
      }
    }
    doR.whenIsActive {
      respStream.valid := io.axiLite.r.valid
      io.axiLite.r.ready := respStream.ready
      when(io.axiLite.r.fire) {
        goto(idle)
      }
    }
  }
}
