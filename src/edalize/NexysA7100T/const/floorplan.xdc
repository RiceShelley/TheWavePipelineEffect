# The MIT License (MIT)
# 
# Copyright (c) 2024 LeafLabs LLC
# 
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
# 
# The above copyright notice and this permission notice shall be included in all
# copies or substantial portions of the Software.
# 
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.

create_pblock generalPblock
add_cells_to_pblock [get_pblocks generalPblock] [get_cells -quiet [list clkwiz wp/ctrl_1 \
    wp/toplevel_ctrl_1_io_dataIn_queue \
    wp/toplevel_toplevel_ctrl_1_io_dataIn_queue_io_pop_queueWithOccupancy \
    wp/toplevel_toplevel_toplevel_wavepipeClkArea_wPipe_io_output_toStream_fifo_io_pop_queue_io_pop_queueWithOccupancy \
    wp/toplevel_toplevel_wavepipeClkArea_wPipe_io_output_toStream_fifo_io_pop_queue \
    wp/toplevel_wavepipeClkArea_wPipe_io_output_toStream_fifo \
    wp/uartToAxiLite4_1]]
resize_pblock [get_pblocks generalPblock] -add {SLICE_X52Y75:SLICE_X71Y99}
resize_pblock [get_pblocks generalPblock] -add {DSP48_X1Y30:DSP48_X1Y39}
resize_pblock [get_pblocks generalPblock] -add {RAMB18_X1Y30:RAMB18_X2Y39}
resize_pblock [get_pblocks generalPblock] -add {RAMB36_X1Y15:RAMB36_X2Y19}


create_pblock pblock_mult
add_cells_to_pblock [get_pblocks pblock_mult] [get_cells -quiet [list wp/wavepipeClkArea_wPipe/mult]]
resize_pblock [get_pblocks pblock_mult] -add {SLICE_X8Y0:SLICE_X51Y49}
resize_pblock [get_pblocks pblock_mult] -add {DSP48_X0Y0:DSP48_X0Y19}



set_property BEL AFF [get_cells wp/wavepipeClkArea_wPipe/mult/dst_0_reg]
set_property LOC SLICE_X47Y4 [get_cells wp/wavepipeClkArea_wPipe/mult/dst_0_reg]
set_property BEL AFF [get_cells wp/wavepipeClkArea_wPipe/mult/dst_1_reg]
set_property LOC SLICE_X47Y5 [get_cells wp/wavepipeClkArea_wPipe/mult/dst_1_reg]
set_property BEL AFF [get_cells wp/wavepipeClkArea_wPipe/mult/dst_2_reg]
set_property LOC SLICE_X47Y6 [get_cells wp/wavepipeClkArea_wPipe/mult/dst_2_reg]
set_property BEL AFF [get_cells wp/wavepipeClkArea_wPipe/mult/dst_3_reg]
set_property LOC SLICE_X47Y7 [get_cells wp/wavepipeClkArea_wPipe/mult/dst_3_reg]
set_property BEL AFF [get_cells wp/wavepipeClkArea_wPipe/mult/dst_4_reg]
set_property LOC SLICE_X47Y8 [get_cells wp/wavepipeClkArea_wPipe/mult/dst_4_reg]
set_property BEL AFF [get_cells wp/wavepipeClkArea_wPipe/mult/dst_5_reg]
set_property LOC SLICE_X47Y9 [get_cells wp/wavepipeClkArea_wPipe/mult/dst_5_reg]
set_property BEL AFF [get_cells wp/wavepipeClkArea_wPipe/mult/dst_6_reg]
set_property LOC SLICE_X47Y10 [get_cells wp/wavepipeClkArea_wPipe/mult/dst_6_reg]
set_property BEL AFF [get_cells wp/wavepipeClkArea_wPipe/mult/dst_7_reg]
set_property LOC SLICE_X47Y11 [get_cells wp/wavepipeClkArea_wPipe/mult/dst_7_reg]
set_property BEL AFF [get_cells wp/wavepipeClkArea_wPipe/mult/dst_8_reg]
set_property LOC SLICE_X47Y12 [get_cells wp/wavepipeClkArea_wPipe/mult/dst_8_reg]
set_property BEL AFF [get_cells wp/wavepipeClkArea_wPipe/mult/dst_9_reg]
set_property LOC SLICE_X47Y13 [get_cells wp/wavepipeClkArea_wPipe/mult/dst_9_reg]
set_property BEL AFF [get_cells wp/wavepipeClkArea_wPipe/mult/dst_10_reg]
set_property LOC SLICE_X47Y14 [get_cells wp/wavepipeClkArea_wPipe/mult/dst_10_reg]
set_property BEL AFF [get_cells wp/wavepipeClkArea_wPipe/mult/dst_11_reg]
set_property LOC SLICE_X47Y15 [get_cells wp/wavepipeClkArea_wPipe/mult/dst_11_reg]
set_property BEL AFF [get_cells wp/wavepipeClkArea_wPipe/mult/dst_12_reg]
set_property LOC SLICE_X47Y16 [get_cells wp/wavepipeClkArea_wPipe/mult/dst_12_reg]
set_property BEL AFF [get_cells wp/wavepipeClkArea_wPipe/mult/dst_13_reg]
set_property LOC SLICE_X47Y17 [get_cells wp/wavepipeClkArea_wPipe/mult/dst_13_reg]
set_property BEL CFF [get_cells wp/wavepipeClkArea_wPipe/mult/dst_14_reg]
set_property LOC SLICE_X47Y18 [get_cells wp/wavepipeClkArea_wPipe/mult/dst_14_reg]
set_property BEL DFF [get_cells wp/wavepipeClkArea_wPipe/mult/dst_15_reg]
set_property LOC SLICE_X47Y18 [get_cells wp/wavepipeClkArea_wPipe/mult/dst_15_reg]



set_property BEL AFF [get_cells {wp/wavepipeClkArea_wPipe/mult/capXReg_reg[0]}]
set_property LOC SLICE_X16Y4 [get_cells {wp/wavepipeClkArea_wPipe/mult/capXReg_reg[0]}]
set_property BEL AFF [get_cells {wp/wavepipeClkArea_wPipe/mult/capXReg_reg[1]}]
set_property LOC SLICE_X16Y5 [get_cells {wp/wavepipeClkArea_wPipe/mult/capXReg_reg[1]}]
set_property BEL AFF [get_cells {wp/wavepipeClkArea_wPipe/mult/capXReg_reg[2]}]
set_property LOC SLICE_X16Y7 [get_cells {wp/wavepipeClkArea_wPipe/mult/capXReg_reg[2]}]
set_property BEL AFF [get_cells {wp/wavepipeClkArea_wPipe/mult/capXReg_reg[3]}]
set_property LOC SLICE_X16Y9 [get_cells {wp/wavepipeClkArea_wPipe/mult/capXReg_reg[3]}]
set_property BEL AFF [get_cells {wp/wavepipeClkArea_wPipe/mult/capXReg_reg[4]}]
set_property LOC SLICE_X16Y11 [get_cells {wp/wavepipeClkArea_wPipe/mult/capXReg_reg[4]}]
set_property BEL AFF [get_cells {wp/wavepipeClkArea_wPipe/mult/capXReg_reg[5]}]
set_property LOC SLICE_X16Y13 [get_cells {wp/wavepipeClkArea_wPipe/mult/capXReg_reg[5]}]
set_property BEL AFF [get_cells {wp/wavepipeClkArea_wPipe/mult/capXReg_reg[6]}]
set_property LOC SLICE_X16Y15 [get_cells {wp/wavepipeClkArea_wPipe/mult/capXReg_reg[6]}]
set_property BEL AFF [get_cells {wp/wavepipeClkArea_wPipe/mult/capXReg_reg[7]}]
set_property LOC SLICE_X16Y17 [get_cells {wp/wavepipeClkArea_wPipe/mult/capXReg_reg[7]}]
set_property BEL AFF [get_cells {wp/wavepipeClkArea_wPipe/mult/capYReg_reg[0]}]
set_property LOC SLICE_X17Y4 [get_cells {wp/wavepipeClkArea_wPipe/mult/capYReg_reg[0]}]
set_property BEL AFF [get_cells {wp/wavepipeClkArea_wPipe/mult/capYReg_reg[1]}]
set_property LOC SLICE_X17Y6 [get_cells {wp/wavepipeClkArea_wPipe/mult/capYReg_reg[1]}]
set_property BEL AFF [get_cells {wp/wavepipeClkArea_wPipe/mult/capYReg_reg[2]}]
set_property LOC SLICE_X17Y8 [get_cells {wp/wavepipeClkArea_wPipe/mult/capYReg_reg[2]}]
set_property BEL AFF [get_cells {wp/wavepipeClkArea_wPipe/mult/capYReg_reg[3]}]
set_property LOC SLICE_X17Y10 [get_cells {wp/wavepipeClkArea_wPipe/mult/capYReg_reg[3]}]
set_property BEL AFF [get_cells {wp/wavepipeClkArea_wPipe/mult/capYReg_reg[4]}]
set_property LOC SLICE_X17Y12 [get_cells {wp/wavepipeClkArea_wPipe/mult/capYReg_reg[4]}]
set_property BEL AFF [get_cells {wp/wavepipeClkArea_wPipe/mult/capYReg_reg[5]}]
set_property LOC SLICE_X17Y14 [get_cells {wp/wavepipeClkArea_wPipe/mult/capYReg_reg[5]}]
set_property BEL AFF [get_cells {wp/wavepipeClkArea_wPipe/mult/capYReg_reg[6]}]
set_property LOC SLICE_X17Y16 [get_cells {wp/wavepipeClkArea_wPipe/mult/capYReg_reg[6]}]
set_property BEL AFF [get_cells {wp/wavepipeClkArea_wPipe/mult/capYReg_reg[7]}]
set_property LOC SLICE_X17Y18 [get_cells {wp/wavepipeClkArea_wPipe/mult/capYReg_reg[7]}]
