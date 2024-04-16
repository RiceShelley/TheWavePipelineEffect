/*

The MIT License (MIT)

Copyright (c) 2024 LeafLabs LLC

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

package wavepipeline.mult

import spinal.core._

class CellM1(sim: Boolean) extends BlackBox {
  val io = new Bundle {
    // Multiplier and multiplicand bits
    val x_in = in Bool ()
    val y_in = in Bool ()
    // Sum of previous partial product
    val v_in = in Bool ()
    // Carry in
    val c_in = in Bool ()
    // Sum out
    val s_out = out Bool ()
    // Carry out
    val c_out = out Bool ()
    // Propagate multiplier and multiplicand bits
    val x_out = out Bool ()
    val y_out = out Bool ()
  }
  noIoPrefix()

  if (sim) {
    addRTLPath("./src/wavepipeline/mult/LUTs.v")
  }
  addRTLPath("./src/wavepipeline/mult/CellM1.v")
}

class CellM2(sim: Boolean) extends BlackBox {
  val io = new Bundle {
    // Multiplier and multiplicand bits
    val x_in = in Bool ()
    val y_in = in Bool ()
    // Sum of previous partial product
    val v_in = in Bool ()
    // Carry in
    val c_in = in Bool ()
    // Sum out
    val s_out = out Bool ()
    // Carry out
    val c_out = out Bool ()
    // Propagate multiplier and multiplicand bits
    val x_out = out Bool ()
    val y_out = out Bool ()
  }
  noIoPrefix()

  if (sim) {
    addRTLPath("./src/wavepipeline/mult/LUTs.v")
  }
  addRTLPath("./src/wavepipeline/mult/CellM2.v")
}

class CellM3(sim: Boolean) extends BlackBox {
  val io = new Bundle {
    // Multiplier and multiplicand bits
    val x_in = in Bool ()
    val y_in = in Bool ()
    // Sum of previous partial product
    val v_in = in Bool ()
    // Carry in
    val c_in = in Bool ()
    // Sum out
    val s_out = out Bool ()
    // Carry out
    val c_out = out Bool ()
    // Propagate multiplier and multiplicand bits
    val x_out = out Bool ()
    val y_out = out Bool ()
  }
  noIoPrefix()

  if (sim) {
    addRTLPath("./src/wavepipeline/mult/LUTs.v")
  }
  addRTLPath("./src/wavepipeline/mult/CellM3.v")
}

object CellDelay {
  def apply(
      sim: Boolean,
      sig: Bool,
      xLoc: Int,
      yLoc: Int,
      name: String
  ): Bool = {
    val delay = new CellDelay(sim)

    delay.setName(name)
    delay.addAttribute("RLOC_ORIGIN", f"X${xLoc}Y${yLoc}")

    delay.io.d_in := sig
    delay.io.d_out
  }
}

class CellDelay(sim: Boolean) extends BlackBox {
  val io = new Bundle {
    val d_in = in Bool ()
    val d_out = out Bool ()
  }
  noIoPrefix()

  if (sim) {
    addRTLPath("./src/wavepipeline/mult/LUTs.v")
  }
  addRTLPath("./src/wavepipeline/mult/CellDelay.v")
}

case class MultBlock(sim: Boolean, dataWidth: Int = 8) extends Component {

  val io = new Bundle {
    val xInput = in Bits (dataWidth bits)
    val yInput = in Bits (dataWidth bits)
    val output = out Bits ((dataWidth * 2) bits)
  }

  case class CellIface() extends Bundle {
    val x = Bool()
    val y = Bool()
    val s = Bool()
    val carry = Bool()
  }

  sealed trait CellType
  case class M1() extends CellType
  case class M2() extends CellType
  case class M3() extends CellType
  case class M4() extends CellType

  val xSliceOrigin = 19
  val ySliceOrigin = 4

  def buildLayer(
      inputs: Seq[(CellIface, CellType)],
      productBits: Seq[Bool] = Seq.empty[Bool]
  ): (Seq[CellIface], Seq[Bool]) = {

    val xSlice = xSliceOrigin + (productBits.length * 2)

    val layer = inputs.zipWithIndex.map { case ((input, cellType), idx) =>
      val ySlice = ySliceOrigin + ((2 * idx) + productBits.length)
      cellType match {
        // M4 cell is logically equivalent to M1
        case M1() | M4() =>
          val cell = new CellM1(sim)
          cell.setName(f"Cell_M1_${productBits.length}_${idx}")
          cell.addAttribute("RLOC_ORIGIN", f"X${xSlice}Y${ySlice}")

          cell.io.x_in := input.x
          cell.io.y_in := input.y
          cell.io.v_in := input.s
          cell.io.c_in := input.carry

          val output = CellIface()
          output.x := cell.io.x_out
          output.y := cell.io.y_out
          output.s := cell.io.s_out
          output.carry := cell.io.c_out
          output

        case M2() =>
          val cell = new CellM2(sim)
          cell.setName(f"Cell_M2_${productBits.length}_${idx}")
          cell.addAttribute("RLOC_ORIGIN", f"X${xSlice}Y${ySlice}")

          cell.io.x_in := input.x
          cell.io.y_in := input.y
          cell.io.v_in := input.s
          cell.io.c_in := input.carry

          val output = CellIface()
          output.x := cell.io.x_out
          output.y := cell.io.y_out
          output.s := cell.io.s_out
          output.carry := cell.io.c_out
          output

        case M3() =>
          val cell = new CellM3(sim)
          cell.setName(f"Cell_M3_${productBits.length}_${idx}")
          cell.addAttribute("RLOC_ORIGIN", f"X${xSlice}Y${ySlice}")

          cell.io.x_in := input.x
          cell.io.y_in := input.y
          cell.io.v_in := input.s
          cell.io.c_in := input.carry

          val output = CellIface()
          output.x := cell.io.x_out
          output.y := cell.io.y_out
          output.s := cell.io.s_out
          output.carry := cell.io.c_out
          output
      }
    }
    // Add 1 LUT of delay to product bits
    val delayedProdBits = productBits.zipWithIndex.map { case (bit, idx) =>
      val delay = new CellDelay(sim)
      delay.setName(f"Cell_Delay_${productBits.length}_${idx}")
      val ySlice = ySliceOrigin + idx
      delay.addAttribute("RLOC_ORIGIN", f"X${xSlice}Y${ySlice}")
      delay.io.d_in := bit
      delay.io.d_out
    }

    (layer, delayedProdBits :+ layer.head.s)
  }

  case class SectionData(
      // last layer
      lLayer: Seq[CellIface],
      // last layer prod bits
      lProdBits: Seq[Bool],
      // last last layer
      llLayer: Seq[CellIface]
  )

  def buildSection(sd: SectionData): SectionData = {
    //////////////////////////////////////////////////////////
    // Build layer 0
    //////////////////////////////////////////////////////////
    val (l0, l0ProdBits) = {
      val lastCarryDelayed = {
        val delay = new CellDelay(sim)

        delay.setName(f"Cell_Carry_Delay_${sd.lProdBits.length}")
        val ySlice =
          ySliceOrigin + (dataWidth * 2) - 1
        val xSlice = xSliceOrigin + ((sd.lProdBits.length - 1) * 2)
        delay.addAttribute("RLOC_ORIGIN", f"X${xSlice}Y${ySlice}")

        delay.io.d_in := sd.llLayer.last.carry
        delay.io.d_out
      }
      val l2PrevPartial = sd.lLayer.map { _.s }.tail :+ lastCarryDelayed
      val l2CarryIn = sd.lLayer.map { _.carry }
      val l2XIn = Seq.fill(l2CarryIn.length)(sd.lLayer.head.x)
      val l2YIn = sd.llLayer.tail.map(_.y)

      val l2Inputs = Seq.fill(l2PrevPartial.length)(CellIface())
      l2Inputs.zip(l2PrevPartial).foreach { case (input, v) => input.s := v }
      l2Inputs.zip(l2CarryIn).foreach { case (input, c) => input.carry := c }
      l2Inputs.zip(l2XIn).foreach { case (input, x) => input.x := x }
      l2Inputs.zip(l2YIn).foreach { case (input, y) => input.y := y }

      // Build layer
      buildLayer(
        l2Inputs.zipWithIndex.map { case (input, idx) =>
          val cellType = if (idx == l2Inputs.length - 1) M4() else M1()
          (input, cellType)
        },
        sd.lProdBits
      )
    }

    //////////////////////////////////////////////////////////
    // Build layer 1
    //////////////////////////////////////////////////////////
    val (l1, l1ProdBits) = {
      val l1PrevPartial = l0.tail.map { _.s }
      val l1CarryIn = l0.init.map { _.carry }
      val l1XIn = sd.lLayer.tail.map { _.x }
      val l1YIn = Seq.fill(l1CarryIn.length)(l0.head.y)

      val l1Inputs = Seq.fill(l1PrevPartial.length)(CellIface())
      l1Inputs.zip(l1PrevPartial).foreach { case (input, v) => input.s := v }
      l1Inputs.zip(l1CarryIn).foreach { case (input, c) => input.carry := c }
      l1Inputs.zip(l1XIn).foreach { case (input, x) => input.x := x }
      l1Inputs.zip(l1YIn).foreach { case (input, y) => input.y := y }

      buildLayer(
        l1Inputs.zipWithIndex.map { case (input, idx) =>
          val cellType = if (idx == l1Inputs.length - 1) M2() else M1()
          (input, cellType)
        },
        l0ProdBits
      )
    }

    // Return this sections outputs
    SectionData(
      lLayer = l1,
      lProdBits = l1ProdBits,
      llLayer = l0
    )
  }

  val gArrayDepth = (dataWidth * 2) - 1

  val capXReg = RegNext(io.xInput)
  val capYReg = RegNext(io.yInput)

  capXReg.addAttribute("DONT_TOUCH", "yes")
  //capXReg.addAttribute("RLOC_ORIGIN", "X17Y11")
  //capXReg.addAttribute("HU_SET", "xCapReg")
  //capXReg.addAttribute("RLOC", "X0Y0")

  capYReg.addAttribute("DONT_TOUCH", "yes")
  //capYReg.addAttribute("RLOC_ORIGIN", "X16Y11")
  //capYReg.addAttribute("HU_SET", "yCapReg")
  //capYReg.addAttribute("RLOC", "X0Y0")

  val x = capXReg
  val y = capYReg

  val (inputL0, inputL0ProdBits) = buildLayer(Seq.tabulate(dataWidth) { bit =>
    val iface = CellIface()
    iface.y := y(bit)
    iface.x := x(0)
    iface.carry := False
    iface.s := False
    val cellType = if (bit == dataWidth - 1) M4() else M1()
    (iface, cellType)
  })

  val (inputL1, inputL1ProdBits) = buildLayer(
    inputs = {
      val prevPartial = inputL0.tail.map { _.s }
      val carryIn = inputL0.init.map { _.carry }
      val xIn = x.asBools.tail
      val yIn = Seq.fill(carryIn.length)(inputL0.head.y)

      val l1Inputs = Seq.fill(prevPartial.length)(CellIface())
      l1Inputs.zip(prevPartial).foreach { case (input, v) => input.s := v }
      l1Inputs.zip(carryIn).foreach { case (input, c) => input.carry := c }
      l1Inputs.zip(xIn).foreach { case (input, x) => input.x := x }
      l1Inputs.zip(yIn).foreach { case (input, y) => input.y := y }

      l1Inputs.zipWithIndex.map { case (input, idx) =>
        val cellType = if (idx == l1Inputs.length - 1) M2() else M1()
        (input, cellType)
      }
    },
    inputL0ProdBits
  )

  def buildNSections(n: Int, sd: SectionData): SectionData = {
    if (n == 0) sd
    else buildNSections(n - 1, buildSection(sd))
  }

  val lastSection = buildNSections(
    n = (gArrayDepth - 3) / 2,
    sd = SectionData(
      lLayer = inputL1,
      lProdBits = inputL1ProdBits,
      llLayer = inputL0
    )
  )

  val lm3 = new CellM3(sim)
  lm3.setName("Cell_LM3")
  val lm3xLoc = xSliceOrigin + (2 * lastSection.lProdBits.length)
  val lm3yLoc = ySliceOrigin + lastSection.lProdBits.length
  lm3.addAttribute(
   "RLOC_ORIGIN",
   f"X${lm3xLoc}Y${lm3yLoc}"
  )

  lm3.io.x_in := lastSection.lLayer.head.x
  lm3.io.y_in := lastSection.llLayer.last.y
  lm3.io.c_in := lastSection.lLayer.head.carry
  lm3.io.v_in := CellDelay(
    sim,
    lastSection.llLayer.last.carry,
    xLoc = lm3xLoc - 2,
    yLoc = lm3yLoc + 1,
    name = "Cell_Carry_Delay_LM3"
  )

  val result =
    lm3.io.c_out ## lm3.io.s_out ## Vec(lastSection.lProdBits.zipWithIndex.map {
      case (bit, idx) =>
        val delay = new CellDelay(sim)

        delay.setName(f"Cell_Delay_${lastSection.lProdBits.length}_${idx}")
        val xSlice = xSliceOrigin + (lastSection.lProdBits.length * 2)
        val ySlice = ySliceOrigin + idx
        delay.addAttribute("RLOC_ORIGIN", f"X${xSlice}Y${ySlice}")

        delay.io.d_in := bit
        delay.io.d_out
    }).asBits

  val capOutput = B(result.asBools.zipWithIndex.map { case (bit, idx) =>
    val yOrigin = 28
    val bitR = Reg(Bool())
    bitR.setName(f"dst_${idx}")
    //bitR.addAttribute("HU_SET", "capOutputReg")
    bitR.addAttribute("DONT_TOUCH", "yes")
    //bitR.addAttribute("RLOC_ORIGIN", "X50Y18")
    //bitR.addAttribute("RLOC", f"X${idx / 8}Y0")
    bitR := bit
    bitR
  })

  io.output := capOutput
}
