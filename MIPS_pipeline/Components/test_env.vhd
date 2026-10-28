library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use ieee.std_logic_unsigned.all;
use ieee.numeric_std.all;

entity test_env is
    Port ( clk : in STD_LOGIC;
           btn : in STD_LOGIC_VECTOR (4 downto 0);
           sw : in STD_LOGIC_VECTOR (15 downto 0);
           led : out STD_LOGIC_VECTOR (15 downto 0);
           an : out STD_LOGIC_VECTOR (7 downto 0);
           cat : out STD_LOGIC_VECTOR (6 downto 0));
end test_env;

architecture Behavioral of test_env is
component IFetch
    Port(
    Jump: in std_logic;
    JumpAddress: in std_logic_vector(31 downto 0);
    PcSrc: in std_logic;
    BranchAddress: in std_logic_vector(31 downto 0);
    en: in std_logic;
    rst: in std_logic;
    clk: in std_logic;
    Instruction: out std_logic_vector(31 downto 0);
    NextAddress: out std_logic_vector(31 downto 0));
end component;
component MPG
    Port( 
    btn : in STD_LOGIC;     
    clk : in STD_LOGIC;     
    enable : out STD_LOGIC);
end component;
component SSD
    Port ( 
    clk : in std_logic;
    digits : in std_logic_vector(31 downto 0);
    an : out std_logic_vector(7 downto 0);
    cat : out std_logic_vector(6 downto 0)
    );
end component;
component UC
    Port(
    Instr : std_logic_vector(5 downto 0);
    RegDst : out std_logic;
    ExtOp : out std_logic;
    ALUSrc : out std_logic;
    Branch : out std_logic;
    Branch_gez : out std_logic;
    Jump : out std_logic;
    ALUOp : out std_logic_vector(1 downto 0);
    MemWrite : out std_logic;
    MemtoReg : out std_logic;
    RegWrite : out std_logic);
end component;
component ID
    Port(
    clk : in std_logic;
    RegWrite : in std_logic;
    Instr : in std_logic_vector(25 downto 0);
    EN : in std_logic;
    ExtOp : in std_logic;
    RD1 : out std_logic_vector(31 downto 0);
    RD2 : out std_logic_vector(31 downto 0);
    WD : in std_logic_vector(31 downto 0);
    ExtInm : out std_logic_vector(31 downto 0);
    func : out std_logic_vector(5 downto 0);
    sa : out std_logic_vector(4 downto 0);
    WA : in std_logic_vector(4 downto 0);
    rt : out std_logic_vector(4 downto 0);
    rd : out std_logic_vector(4 downto 0));
end component;
component EX
    Port(
    RD1 : in std_logic_vector(31 downto 0);
    ALUSrc : in std_logic;
    RD2 : in std_logic_vector(31 downto 0);
    ExtInm : in std_logic_vector(31 downto 0);
    sa : in std_logic_vector(4 downto 0);
    func : in std_logic_vector(5 downto 0);
    ALUOp : in std_logic_vector(1 downto 0);
    nextAddress : std_logic_vector(31 downto 0);
    gez : out std_logic;
    zero : out std_logic;
    ALURes : out std_logic_vector(31 downto 0);
    BranchAddress : out std_logic_vector(31 downto 0);
    rt : in std_logic_vector(4 downto 0);
    rd : in std_logic_vector(4 downto 0);
    RegDst : in std_logic;
    rWA : out std_logic_vector(4 downto 0));
end component;
component MEM
    Port(
    MemWrite : in std_logic;                     
    ALUResin : std_logic_vector(31 downto 0);    
    RD2 : in std_logic_vector(31 downto 0);      
    clk : in std_logic;                          
    en : in std_logic;                           
    MemData : out std_logic_vector(31 downto 0); 
    ALUResout : out std_logic_vector(31 downto 0)
    );
end component;
signal enable_mpg : std_logic := '0';
signal JumpAddress : std_logic_vector(31 downto 0);
signal PcSrc : std_logic;
signal BranchAddress : std_logic_vector(31 downto 0);
signal InstructionIFetch : std_logic_vector(31 downto 0);
signal NextAddress : std_logic_vector(31 downto 0);
signal RegDst : std_logic;
signal ExtOp : std_logic;
signal ALUSrc : std_logic;
signal Branch : std_logic;
signal Branch_gez : std_logic;
signal Jump : std_logic;--+IFetch
signal ALUOp : std_logic_vector(1 downto 0);
signal MemWrite : std_logic;
signal MemtoReg : std_logic;
signal RegWrite : std_logic;
signal RD1 : std_logic_vector(31 downto 0);
signal RD2 : std_logic_vector(31 downto 0);
signal WD : std_logic_vector(31 downto 0);
signal ExtInm : std_logic_vector(31 downto 0);
signal func : std_logic_vector(5 downto 0);
signal sa : std_logic_vector(4 downto 0);
signal zero : std_logic;
signal gez : std_logic;
signal ALURes : std_logic_vector(31 downto 0);
signal MemData: std_logic_vector(31 downto 0);
signal ALUResout : std_logic_vector(31 downto 0);
signal output : std_logic_vector(15 downto 0);
signal mux : std_logic_vector(31 downto 0);
signal rd : std_logic_vector(4 downto 0);
signal rt : std_logic_vector(4 downto 0);
signal wa : std_logic_vector(4 downto 0);
signal rwa : std_logic_vector(4 downto 0);
--Declaram Registrele Pipeline
--Instruction Fetch->Instruction Decoder
signal Instruction_IF_ID : std_logic_vector(31 downto 0);
signal nextAddress_IF_ID : std_logic_vector(31 downto 0);
--Instruction Decoder->Execute
signal RegDst_ID_EX : std_logic;
signal ALUSrc_ID_EX : std_logic;
signal Branch_ID_EX : std_logic;
signal Branch_gez_ID_EX : std_logic;
signal ALUOp_ID_EX : std_logic_vector(1 downto 0);
signal MemWrite_ID_EX : std_logic;
signal MemtoReg_ID_EX : std_logic;
signal RegWrite_ID_EX : std_logic;
signal RD1_ID_EX : std_logic_vector(31 downto 0);
signal RD2_ID_EX : std_logic_vector(31 downto 0);
signal ExtInm_ID_EX : std_logic_vector(31 downto 0);
signal func_ID_EX : std_logic_vector(5 downto 0);
signal sa_ID_EX : std_logic_vector(4 downto 0);
signal rd_ID_EX : std_logic_vector(4 downto 0);
signal rt_ID_EX : std_logic_vector(4 downto 0);
signal nextAddress_ID_EX : std_logic_vector(31 downto 0);
--Execute->Memory
signal Branch_EX_MEM : std_logic;
signal Branch_gez_EX_MEM : std_logic;
signal MemWrite_EX_MEM : std_logic;
signal MemtoReg_EX_MEM : std_logic;
signal RegWrite_EX_MEM : std_logic;
signal zero_EX_MEM : std_logic;
signal gez_EX_MEM : std_logic;
signal BranchAddress_EX_MEM : std_logic_vector(31 downto 0);
signal ALURes_EX_MEM : std_logic_vector(31 downto 0);
signal WA_EX_MEM : std_logic_vector(4 downto 0);
signal RD2_EX_MEM : std_logic_vector(31 downto 0);
--Memory->Write Back
signal MemtoReg_MEM_WB : std_logic;
signal RegWrite_MEM_WB : std_logic;
signal ALURes_MEM_WB : std_logic_vector(31 downto 0);
signal MemData_MEM_WB : std_logic_vector(31 downto 0);
signal WA_MEM_WB : std_logic_vector(4 downto 0);

begin
JumpAddress<=NextAddress_IF_ID(31 downto 28)&Instruction_IF_ID(25 downto 0)&"00";
PCSrc<=(zero_EX_MEM and Branch_EX_MEM) or (gez_EX_MEM and Branch_gez_EX_MEM);
IFetch_inst:
IFetch port map(
    clk => clk,
    rst => btn(1),
    en => enable_mpg,
    BranchAddress => BranchAddress_EX_MEM,
    PcSrc => PCSrc,
    JumpAddress => JumpAddress,
    Jump => Jump,
    Instruction => InstructionIFetch,
    NextAddress => NextAddress
);
MPG_inst:
MPG port map(
    clk => clk,
    enable =>enable_mpg,
    btn => btn(0)
);
UC_inst: UC port map(
    Instr => Instruction_IF_ID(31 downto 26),
    RegDst => RegDst,
    ExtOp => ExtOp,
    ALUSrc => ALUSrc,
    Branch => Branch,
    Branch_gez => Branch_gez,
    Jump => Jump,
    ALUOp => ALUOp,
    MemWrite => MemWrite,
    MemtoReg => MemtoReg,
    RegWrite => RegWrite
);
ID_inst: ID port map(
    clk => clk,
    RegWrite => RegWrite_MEM_WB,
    Instr => Instruction_IF_ID(25 downto 0),
    EN => enable_mpg,
    ExtOp => ExtOp,
    RD1 => RD1,
    RD2 => RD2,
    WD => WD,
    ExtInm => ExtInm,
    func => func,
    sa => sa,
    rd=>rd,
    rt=>rt,
    wa=>wa_MEM_WB
);
EX_inst: EX port map(
    ALUSrc => ALUSrc_ID_EX,
    RD1=>RD1_ID_EX,
    RD2=>RD2_ID_EX,
    ExtInm=>ExtInm_ID_EX,
    func=>func_ID_EX,
    sa=>sa_ID_EX,
    ALUOp=>ALUOp_ID_EX,
    zero=>zero,
    gez=>gez,
    BranchAddress=>BranchAddress,
    ALURes=>ALURes,
    rd=>rd_ID_EX,
    rt=>rt_ID_EX,
    nextAddress=>nextAddress_ID_EX,
    rwa=>rwa,
    RegDst=>RegDst_ID_EX
);
MEM_inst: MEM port map(
    ALUResin=>ALURes_EX_MEM,
    EN=>enable_mpg,
    RD2=>RD2_EX_MEM,
    clk=>clk,
    MemWrite=>MemWrite_EX_MEM,
    ALUResout=>ALUResout,
    MemData=>MemData
);
--Write Back
process(ALURes_MEM_WB,MemData_MEM_WB,MemtoReg_MEM_WB)
begin
    if MemtoReg_MEM_WB='0' then
        WD<=ALURes_MEM_WB;
    else
        WD<=MemData_MEM_WB;
    end if;
end process;
process(sw(7 downto 5),InstructionIFetch,NextAddress,RD1,RD2,ExtInm,ALURes,
MemData,WD)
begin
    case sw(7 downto 5) is
        when "000" => mux <= InstructionIFetch;
        when "001" => mux <= nextAddress;
        when "010" => mux <= RD1;
        when "011" => mux <= RD2;
        when "100" => mux <= ExtInm;
        when "101" => mux <= ALURes;--EX
        when "110" => mux <= MemData;--MEM
        when "111" => mux <= WD;--ID
        when others => mux <= X"11111111";
    end case;
end process;
SSD_inst:
SSD port map(clk,mux,an,cat);
--Leduri
led<=
"000"
&Branch_gez--ledul 12
&zero--ledul 11
&gez--ledul 10
&RegDst--ledul 9
&ExtOp--ledul 8
&ALUSrc--ledul 7
&Branch--ledul 6
&Jump--ledul 5
&ALUOp--ledurile 4 si 3
&MemWrite--ledul 2
&MemtoReg--ledul 1
&RegWrite;--ledul 0
--atribuire registre pipeline
process(clk)
begin
if rising_edge(clk) then
    if enable_mpg = '1' then
        --Instruction Fetch-> Instruction Decoder
        Instruction_IF_ID<=InstructionIFetch;
        nextAddress_IF_ID<=nextAddress;--de la IFetch
        --Instruction Decoder-> Execute
        RegDst_ID_EX<=RegDst;
        ALUSrc_ID_EX<=ALUSrc;
        Branch_ID_EX<=Branch;
        Branch_gez_ID_EX<=Branch_gez;
        ALUOp_ID_EX<=ALUOp;
        MemWrite_ID_EX<=MemWrite;
        MemtoReg_ID_EX<=MemtoReg;
        RegWrite_ID_EX<=RegWrite;
        RD1_ID_EX<=RD1;
        RD2_ID_EX<=RD2;
        ExtInm_ID_EX<=ExtInm;
        func_ID_EX<=func;
        sa_ID_EX<=sa;
        rd_ID_EX<=rd;
        rt_ID_EX<=rt;
        nextAddress_ID_EX<=nextAddress_IF_ID;
        --Execute->Memory
        Branch_EX_MEM<=Branch_ID_EX;
        Branch_gez_EX_MEM<=Branch_gez_ID_EX;
        MemWrite_EX_MEM<=MemWrite_ID_EX;
        MemtoReg_EX_MEM<=MemtoReg_ID_EX;
        RegWrite_EX_MEM<=RegWrite_ID_EX;
        zero_EX_MEM<=zero;
        BranchAddress_EX_MEM<=BranchAddress;
        ALURes_EX_MEM<=ALURes;
        WA_EX_MEM<=rwa;
        RD2_EX_MEM<=RD2_ID_EX;
        --Memory->Write Back
        MemtoReg_MEM_WB<=MemtoReg_EX_MEM;
        RegWrite_MEM_WB<=RegWrite_EX_MEM;
        ALURes_MEM_WB<=ALUResOut;
        MemData_MEM_WB<=MemData;
        WA_MEM_WB<=WA_EX_MEM;
    end if;
end if;
end process;
end Behavioral;
