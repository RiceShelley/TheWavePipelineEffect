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

`timescale 1ns / 1ps

(* DONT_TOUCH = "yes" *) module CellM3 (
    // Multiplier and multiplicand bits
    input  wire x_in,
    input  wire y_in,
    // Sum of previous partial product
    input  wire v_in,
    // Carry in
    input  wire c_in,
    // Sum out
    output wire s_out,
    // Carry out
    output wire c_out,
    // Propagate multiplier and multiplicand bits
    output wire x_out,
    output wire y_out
);

  //////////////////////////////////////////////////////////
  // Proigate x_in and y_in to output with 1 lut of delay
  //////////////////////////////////////////////////////////

  // LUT1: 1-input Look-Up Table with general output (Mapped to a LUT6)
  //       Artix-7
  // Xilinx HDL Language Template, version 2019.2
  (* DONT_TOUCH = "yes", BEL="A6LUT", RLOC="X0Y0" *)
  LUT1 #(
      .INIT(2'b10)  // Specify LUT Contents
  ) LUT1_inst_prop_x (
      .O (x_out),  // LUT general output
      .I0(x_in)    // LUT input
  );
  // End of LUT1_inst instantiation

  // LUT1: 1-input Look-Up Table with general output (Mapped to a LUT6)
  //       Artix-7
  // Xilinx HDL Language Template, version 2019.2
  (* DONT_TOUCH = "yes", BEL="B6LUT", RLOC="X0Y0" *)
  LUT1 #(
      .INIT(2'b10)  // Specify LUT Contents
  ) LUT1_inst_prop_y (
      .O (y_out),  // LUT general output
      .I0(y_in)    // LUT input
  );
  // End of LUT1_inst instantiation

  //////////////////////////////////////////////////////////
  // Compute sum
  //////////////////////////////////////////////////////////
  // LUT4: 4-input Look-Up Table with general output (Mapped to a LUT6)
  //       Artix-7
  // Xilinx HDL Language Template, version 2019.2
  (* DONT_TOUCH = "yes", BEL="C6LUT", RLOC="X0Y0" *)
  LUT4 #(
      .INIT(16'h956a)  // Specify LUT Contents
  ) LUT4_inst_compute_sum (
      .O (s_out),  // LUT general output
      .I0(c_in),   // LUT input
      .I1(x_in),   // LUT input
      .I2(y_in),   // LUT input
      .I3(v_in)    // LUT input
  );
  // End of LUT4_inst instantiation

  //////////////////////////////////////////////////////////
  // Compute carry
  //////////////////////////////////////////////////////////
  // LUT4: 4-input Look-Up Table with general output (Mapped to a LUT6)
  //       Artix-7
  // Xilinx HDL Language Template, version 2019.2
  (* DONT_TOUCH = "yes", BEL="D6LUT", RLOC="X0Y0" *)
  LUT4 #(
      .INIT(16'hbf2a)  // Specify LUT Contents
  ) LUT4_inst_compute_carry (
      .O (c_out),  // LUT general output
      .I0(c_in),   // LUT input
      .I1(x_in),   // LUT input
      .I2(y_in),   // LUT input
      .I3(v_in)    // LUT input
  );
  // End of LUT4_inst instantiation

endmodule

