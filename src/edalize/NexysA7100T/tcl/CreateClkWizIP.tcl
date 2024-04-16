create_ip -name clk_wiz -vendor xilinx.com -library ip -version 6.0 -module_name clk_wiz
set_property -dict [list CONFIG.Component_Name {clk_wiz} \
    CONFIG.CLKOUT2_USED {true} \
    CONFIG.PRIMARY_PORT {clk_in} \
    CONFIG.CLK_OUT1_PORT {sysClk} \
    CONFIG.CLK_OUT2_PORT {wavepipeClk} \
    CONFIG.CLKOUT2_REQUESTED_OUT_FREQ {130.00} \
    CONFIG.USE_RESET {false} \
    CONFIG.MMCM_CLKFBOUT_MULT_F {6.500} \
    CONFIG.MMCM_CLKOUT0_DIVIDE_F {6.500} \
    CONFIG.MMCM_CLKOUT1_DIVIDE {5} \
    CONFIG.NUM_OUT_CLKS {2} \
    CONFIG.CLKOUT1_JITTER {145.553} \
    CONFIG.CLKOUT1_PHASE_ERROR {124.502} \
    CONFIG.CLKOUT2_JITTER {137.898} \
    CONFIG.CLKOUT2_PHASE_ERROR {124.502}] [get_ips clk_wiz]