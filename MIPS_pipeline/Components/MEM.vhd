library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use ieee.std_logic_unsigned.all;

entity MEM is
  Port (
  MemWrite : in std_logic;
  ALUResin : std_logic_vector(31 downto 0);
  RD2 : in std_logic_vector(31 downto 0);
  clk : in std_logic;
  en : in std_logic;
  MemData : out std_logic_vector(31 downto 0);
  ALUResout : out std_logic_vector(31 downto 0)
   );
end MEM;

architecture Behavioral of MEM is
type data_memory is array(0 to 63) of std_logic_vector(31 downto 0);
signal MEM : data_memory :=(
    X"00000000",--00--aici se va stoca contorul de numere(va deveni 4)
    X"00000001",--01
    X"00000002",--02
    X"00000003",--03
    X"00000005",--04--de aici citim N(numarul de elemente)
    X"00000006",--05
    X"00000007",--06
    X"00000008",--07
    X"FFFFFFF1",--08--de aici citim vectorul de N elemente #rezultat++
    X"00000002",--09
    X"00000003",--10 #rezultat++
    X"FFFFFFFF",--11 #rezultat++
    X"FFFFFFFB",--12=>ultimul element din vector #rezultat++
    X"00000003",--13
    X"00000004",--14
    X"00000005",--15
    others => X"0000000"
);

begin
process(clk)
begin
    if rising_edge(clk) then
        if en='1' and MemWrite = '1' then
            MEM(conv_integer(ALUResin(7 downto 2)))<=RD2;
        end if;
    end if;
end process;
MemData <= MEM(conv_integer(ALUResin(7 downto 2)));
ALUResout <= ALUResin;
end Behavioral;