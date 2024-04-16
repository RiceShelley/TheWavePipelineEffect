# The MIT License (MIT)
# 
# Copyright (c) 2024-present Rice Shelley
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

import os
from edalize import *

local_verilog_files = list(
    map(
        lambda file: {"name": "../src/" + file, "file_type": "verilogSource"},
        ["Top.v"],
    )
)
spinal_verilog_files = list(
    map(
        lambda file: {
            "name": "../../../../hw/gen/" + file,
            "file_type": "verilogSource",
        },
        ["wavepipe_top.v", "wavepipe_top_bb.v"],
    )
)
verilog_files = local_verilog_files + spinal_verilog_files

tcl_files = map(
    lambda file: {
        "name": "../tcl/" + file,
        "file_type": "tclSource",
    },
    ["CreateClkWizIP.tcl"],
)

constraint_files = map(
    lambda file: {
        "name": "../const/" + file,
        "file_type": "xdc",
    },
    ["pins.xdc", "clocks.xdc", "floorplan.xdc"],
)


part = "xc7a100tcsg324"

tool_options = {"part": part}

project_name = "wavepipe_project"
top_level = "top"
work_root = "./build/"

print("CUR WORKING DIR")
print(os.getcwd())

files = [*verilog_files, *tcl_files, *constraint_files]
edam = {
    "files": files,
    "name": project_name,
    "toplevel": top_level,
    "tool_options": {"vivado": tool_options},
}

tool = "vivado"
backend = get_edatool(tool)(edam=edam, work_root=work_root)

os.makedirs(name=work_root, exist_ok=True)
backend.configure()

backend.build()
backend.run()
