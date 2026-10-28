# 32-Bit Single-Cycle MIPS Processor

A modular implementation of a 32-bit single-cycle MIPS architecture written in VHDL. This project integrates a complete datapath, a hardwired control unit, debounced inputs, and real-time hardware visualization for FPGA deployment.

## Architecture and Components

The system follows standard RISC design principles, broken down into dedicated VHDL modules inside the `Components/` directory:

* **`test_env.vhdu` (Top-Level Entity):**
  Integrates all datapath stages, clock management, push-button inputs, switches, and the Seven-Segment Display.

* **`IFetch.vhdu` (Instruction Fetch):**
  Manages the Program Counter (`PC`), computes sequential instruction addresses (`PC + 4`), and handles branch and jump target address generation.

* **`ID.vhdu` (Instruction Decode):**
  Contains the 32x32-bit Register File, sign/zero extension logic, and instruction field slicing (`rs`, `rt`, `rd`, `imm`).

* **`UC.vhdu` (Control Unit):**
  Decodes instruction opcodes and function fields to generate all necessary datapath control flags (ALUSrc, RegDst, MemToReg, RegWrite, MemWrite, Branch, Jump).

* **`EX.vhdu` (Execution / ALU):**
  Houses the Arithmetic Logic Unit (ALU) capable of performing arithmetic, logical, and comparison operations, alongside branch offset calculation.

* **`MEM.vhdu` (Memory Access):**
  Interfaces with Data Memory for read (`lw`) and write (`sw`) operations.

* **`MPG.vhdu` (Monopulse Generator):**
  A robust debouncing circuit used to generate clean clock/step pulses from asynchronous board buttons.

* **`SSD.vhdu` (Seven-Segment Display):**
  A multiplexed display driver allowing real-time observation of internal registers, the Program Counter, or ALU outputs directly on an FPGA board.

## Project Directory Structure

```
MIPS_single_cycle/
│
├── Components/
│   ├── EX.vhdu                 # Execution Unit & ALU
│   ├── ID.vhdu                 # Instruction Decode & Register File
│   ├── IFetch.vhdu             # Instruction Fetch & PC Management
│   ├── MEM.vhdu                # Data Memory Interface
│   ├── MPG.vhdu                # Push-button Debouncer
│   ├── SSD.vhdu                # Seven-Segment Display Driver
│   ├── UC.vhdu                 # Main Control Unit
│   └── test_env.vhdu           # Top-Level System Integration
│
└── Schemas/
    └── RTL_schema_single_cycle.png  # Complete RTL Datapath Schematic
```

## Getting Started and Usage

1. **Prerequisites:**
   * A VHDL synthesis and simulation environment (e.g., Xilinx Vivado or ISE).
   * An FPGA development board configured with appropriate constraints for clocks, switches, buttons, and SSD outputs.

2. **Setup:**
   * Import all `.vhdu` files into your project workspace.
   * Set `test_env.vhdu` as the top-level design entity.

3. **Reference:**
   * Review `Schemas/RTL_schema_single_cycle.png` for a visual map of signal connections and bus widths.

4. **Implementation:**
   * Run synthesis, implementation, and generate the bitstream to program your target FPGA. Use the Monopulse Generator (`MPG`) to step through instructions instruction-by-instruction while monitoring states on the Seven-Segment Display (`SSD`).