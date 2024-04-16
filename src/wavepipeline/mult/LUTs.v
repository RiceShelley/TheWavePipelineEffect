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

module LUT1 #(
    parameter INIT = 2'b00
) (
    output wire O,
    input  wire I0
);

  wire [1:0] lut = INIT;
  assign O = lut[I0];

endmodule

module LUT4 #(
    parameter INIT = 16'h0000
) (
    output wire O,
    input  wire I0,
    input  wire I1,
    input  wire I2,
    input  wire I3
);

  wire [15:0] lut = INIT;
  wire [ 3:0] addr = {I3, I2, I1, I0};
  assign O = lut[addr];

endmodule
