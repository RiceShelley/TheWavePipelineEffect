nexysA7_gen_verilog:
	sbt "runMain wavepipeline.TopGenVerilog"

nexysA7_gen_bitstream: gen_verilog
	cd ./src/edalize/NexysA7100T && python3 Build.py