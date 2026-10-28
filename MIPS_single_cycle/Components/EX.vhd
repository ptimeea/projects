library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use ieee.numeric_std.all;
use ieee.std_logic_unsigned.all;

entity EX is
  Port (
    RD1 : in std_logic_vector(31 downto 0);
    ALUSrc : in std_logic;
    RD2 : in std_logic_vector(31 downto 0);
    Ext_inm : in std_logic_vector(31 downto 0);
    sa : in std_logic_vector(4 downto 0);
    func : in std_logic_vector(5 downto 0);
    ALUOp : in std_logic_vector(1 downto 0);
    nextAddress : std_logic_vector(31 downto 0);
    gez : out std_logic;
    zero : out std_logic;
    ALURes : out std_logic_vector(31 downto 0);
    BranchAddress : out std_logic_vector(31 downto 0)
);
end EX;

architecture Behavioral of EX is
signal ALUCtrl : std_logic_vector(2 downto 0);
signal A,B,C: std_logic_vector(31 downto 0);
begin
process(func,ALUOp)
begin
case ALUOp is
    when "10" =>
        case func is
            when "100000" => ALUCtrl<="000";--adunare
            when "100010" => ALUCtrl<="100";--scadere
            when "000000" => ALUCtrl<="011";--shiftare stanga
            when "000010" => ALUCtrl<="101";--shiftare dreapta
            when "100100" => ALUCtrl<="001";--and
            when "100101" => ALUCtrl<="010";--or
            when "000011" => ALUCtrl<="111";--shiftare aritmetica
            when "100110" => ALUCtrl<="110";--xor
            when others => ALUCtrl<=(others=>'0');
        end case;
    when "00" => ALUCtrl<="000";--adunare
    when "11" => ALUCtrl<="010";--or
    when "01" => ALUCtrl<="100";--scadere
    when others => ALUCtrl<=(others=> '0');
end case;
end process;
process(RD2,Ext_Inm,ALUSrc)
begin
    if ALUSrc = '0' then
        B <= RD2;
    else
        B <= Ext_Inm;
    end if;
end process;
A<=RD1;
process(A,B,sa,ALUCtrl)
begin
    case ALUCtrl is
        when "000" => C<=A+B;
        when "100" => C<=A-B;
        when "011" => C<=to_stdlogicvector(to_bitvector(B) sll conv_integer(sa));
        when "101" => C<=to_stdlogicvector(to_bitvector(B) srl conv_integer(sa));
        when "001" => C<=A and B;
        when "010" => C<=A or B;
        when "111" => C<=to_stdlogicvector(to_bitvector(B) sra conv_integer(sa));
        when "110" => C<=A xor B;
        when others => C<=(others => '0');
    end case;
end process;
ALURes <= C;
gez<=not C(31);
zero<='1' when C = X"00000000" else '0';
BranchAddress <= Ext_Inm(29 downto 0)&"00" + nextAddress;
end Behavioral;
