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
import spinal.lib.com.uart._
import spinal.lib.bus.amba4.axilite._

object UartToAxiLite4 {
  def apply(
      baudrate: HertzNumber,
      axi4LiteConfig: AxiLite4Config,
      uartPort: Uart
  ): AxiLite4 = {
    val comp =
      UartToAxiLite4(baudrate = baudrate, axi4LiteConfig = axi4LiteConfig)
    comp.io.uart <> uartPort
    comp.io.axiLite
  }
}

case class UartToAxiLite4(baudrate: HertzNumber, axi4LiteConfig: AxiLite4Config)
    extends Component {

  val io = new Bundle {
    val uart = master(Uart(ctsGen = false, rtsGen = false))

    val axiLite = master(AxiLite4(axi4LiteConfig))
  }

  //////////////////////////////////////////////////////////
  // Configure UART controller
  //////////////////////////////////////////////////////////
  val uCtrl = new UartCtrl(
    UartCtrlGenerics(
      ctsGen = false,
      rtsGen = false,
      preSamplingSize = 2,
      samplingSize = 5,
      postSamplingSize = 4
    )
  )

  uCtrl.io.config.setClockDivider(baudrate)
  uCtrl.io.config.frame.dataLength := 7
  uCtrl.io.config.frame.parity := UartParityType.NONE
  uCtrl.io.config.frame.stop := UartStopType.ONE
  uCtrl.io.writeBreak := False

  // Connect UART port to top level
  uCtrl.io.uart <> io.uart

  val axi4Ctrl = StreamToAxiLite4(config = axi4LiteConfig)
  io.axiLite <> axi4Ctrl.io.axiLite

  StreamWidthAdapter(
    input = uCtrl.io.read,
    output = axi4Ctrl.io.cmd,
    padding = true
  )

  StreamWidthAdapter(
    input = axi4Ctrl.io.resp.stage(),
    output = uCtrl.io.write,
    padding = true
  )

}
