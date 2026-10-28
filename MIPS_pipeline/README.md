# 32-Bit Pipelined MIPS Processor

A modular implementation of a 32-bit pipelined MIPS architecture written in VHDL. This project integrates a complete 5-stage pipelined datapath (IF, ID, EX, MEM, WB), pipeline registers, hazard management, a hardwired control unit, debounced inputs, and real-time hardware visualization for FPGA deployment.

## Architecture and Components

The system follows standard pipelined RISC design principles, broken down into dedicated VHDL modules inside the `Components/` directory, separated by pipeline registers:

* **`test_env.vhdu` (Top-Level Entity):**
  Integrates all pipeline stages, pipeline registers, clock management, push-button inputs, switches, and the Seven-Segment Display.

* **`IFetch.vhdu` (Instruction Fetch Stage):**
  Manages the Program Counter (`PC`), computes sequential instruction addresses (`PC + 4`), and handles branch and jump target address generation.

* **`ID.vhdu` (Instruction Decode Stage):**
  Contains the 32x32-bit Register File, sign/zero extension logic, and instruction field slicing (`rs`, `rt`, `rd`, `imm`).

* **`UC.vhdu` (Control Unit):**
  Decodes instruction opcodes and function fields to generate all necessary datapath control flags propagated down the pipeline stages.

* **`EX.vhdu` (Execution Stage / ALU):**
  Houses the Arithmetic Logic Unit (ALU) capable of performing arithmetic, logical, and comparison operations, alongside branch offset calculation.

* **`MEM.vhdu` (Memory Access Stage):**
  Interfaces with Data Memory for read (`lw`) and write (`sw`) operations.

* **`MPG.vhdu` (Monopulse Generator):**
  A robust debouncing circuit used to generate clean clock/step pulses from asynchronous board buttons.

* **`SSD.vhdu` (Seven-Segment Display):**
  A multiplexed display driver allowing real-time observation of internal registers, pipeline register contents, the Program Counter, or ALU outputs directly on an FPGA board.

## Project Directory Structure

```
MIPS_pipeline/
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
    └── RTL_schema_pipeline.png # Complete Pipelined RTL Datapath Schematic

```

## Getting Started and Usage

1. **Prerequisites:**

   * A VHDL synthesis and simulation environment (e.g., Xilinx Vivado or ISE).

   * An FPGA development board configured with appropriate constraints for clocks, switches, buttons, and SSD outputs.

2. **Setup:**

   * Import all `.vhdu` files into your project workspace.

   * Set `test_env.vhdu` as the top-level design entity.

3. **Reference:**

   * Review `Schemas/RTL_schema_pipeline.png` for a visual map of pipeline registers, signal connections, and bus widths.

4. **Implementation:**

   * Run synthesis, implementation, and generate the bitstream to program your target FPGA. Use the Monopulse Generator (`MPG`) to step through clock cycles and observe instruction execution across pipeline stages on the Seven-Segment Display (`SSD`).