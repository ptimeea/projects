library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use ieee.std_logic_unsigned.all;


entity ID is
  Port ( 
    clk : in std_logic;
    RegWrite : in std_logic;
    Instr : in std_logic_vector(25 downto 0);
    RegDst : in std_logic;
    EN : in std_logic;
    ExtOp : in std_logic;
    RD1 : out std_logic_vector(31 downto 0);
    RD2 : out std_logic_vector(31 downto 0);
    WD : in std_logic_vector(31 downto 0);
    ExtInm : out std_logic_vector(31 downto 0);
    func : out std_logic_vector(5 downto 0);
    sa : out std_logic_vector(4 downto 0)
  );
end ID;

--blocul de registre($0 va fi intotdeauna 0)
architecture Behavioral of ID is
type reg_array is array(0 to 31) of std_logic_vector(31 downto 0);
    signal RF : reg_array := (
            others => X"00000000"
    );
signal WriteAddr : std_logic_vector(4 downto 0);
signal ReadAddress1, ReadAddress2 : std_logic_vector(4 downto 0);
signal mux_in_1,mux_in_2,mux_out : std_logic_vector(4 downto 0);
begin
process(clk)
begin
    if rising_edge(clk) then
        if en = '1' and RegWrite='1' then
            RF(conv_integer(WriteAddr))<=WD;
        end if;
    end if;
end process;
process(mux_in_1,mux_in_2,RegDst)
begin
    if RegDst = '0' then
        mux_out <= mux_in_1;
    else
        mux_out <= mux_in_2;
     end if;
end process;
ReadAddress1 <= Instr(25 downto 21);
ReadAddress2 <= Instr(20 downto 16);
mux_in_1<=Instr(20 downto 16);
mux_in_2<=Instr(15 downto 11);
--output-uri
RD1<=RF(conv_integer(ReadAddress1));
RD2<=RF(conv_integer(ReadAddress2));
sa<=Instr(10 downto 6);
func<=Instr(5 downto 0);
ExtInm(15 downto 0) <= Instr(15 downto 0);
ExtInm(31 downto 16) <= (others => Instr(15)) when ExtOp = '1' else(others => '0');
WriteAddr<=mux_out;
end Behavioral;
