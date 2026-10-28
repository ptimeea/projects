library IEEE;
use IEEE.STD_LOGIC_1164.ALL;
use IEEE.STD_LOGIC_UNSIGNED.ALL;

entity IFetch is
Port(
 Jump: in std_logic;
 PcSrc: in std_logic;
 JumpAddress: in std_logic_vector(31 downto 0);
 BranchAddress: in std_logic_vector(31 downto 0);
 Instruction: out std_logic_vector(31 downto 0);
 NextAddress: out std_logic_vector(31 downto 0);
  clk: in std_logic;
 en: in std_logic;
 rst: in std_logic
 );
end IFetch;

architecture Behavioral of IFetch is

type memorie_rom is array (0 to 31) of std_logic_vector(31 downto 0);
signal rom : memorie_rom :=(
--Avem un vector de N elemente si vrem sa numaram elementele impare. N se gaseste la indicele 4,
--iar elementele incep de la indicele 8
b"001000_00000_00001_0000000000000000",--X"2001_0000"--00  addi $1,$0,0 #i=0, contorul buclei
b"001000_00000_00010_0000000000100000",--X"2002_0020"--01  addi $2,$0,32 #initializarea indexului locatiei de memorie pentru vector
b"100011_00000_00011_0000000000010000",--X"8C03_0010"--02  lw   $3,16($0) #incarcam valoarea lui N
b"000000_00000_00011_00100_00000_100000",--X"0003_2020"--03  add  $4,$0,$3 #numarul maxim de iteratii=N
b"000000_00000_00000_00101_00000_100000",--X"0000_2820"--04  add  $5,$0,$0 # rezultat=0
b"000100_00001_00100_0000000000001000",--X"1024_0008"--05  beq $1,$4,8 #sarim peste ... instructiuni daca nu sunt egale valorile
b"100011_00010_00110_0000000000000000",--X"8C46_0000"--06      lw $6,0($2) #luam elementul curent din vector
b"001000_00000_01010_0000000000000001",--X"200A_0001"--07    addi $10,$0,1
b"000000_00110_01010_00111_00000_100100",--X"00CA_3824"--08      and $7,$6,$10 #facem and cu 1 pentru a verifica ultimul bit
b"000100_00111_00000_0000000000000001",--X"10E0_0001--09      beq $7,$0,1 #daca e zero, inseamna ca numarul este par  
b"001000_00101_00101_0000000000000001",--X"20A5_0001"--10      addi $5,$5,1 #incrementam contorul cu 1
b"001000_00010_00010_0000000000000100",--X"2042_0004"--11      addi $2,$2,4 #indexul urmatorului element din sir
b"001000_00001_00001_0000000000000001",--X"2021_0001"--12      addi $1,$1,1 #i=i+1
b"000010_00000000000000000000000101",--X"0800_0005"----13  j 5
b"101011_00000_00101_0000000000000000",--X"AC05_0000"--14  sw $5,0($0) #stocam la adresa 0 rezultatul
others => X"00000000"
);
signal mux1:std_logic_vector(31 downto 0):=(others=>'0');
signal mux2:std_logic_vector(31 downto 0):=(others=>'0');
signal sum:std_logic_vector(31 downto 0):=(others=>'0');
signal q:std_logic_vector(31 downto 0):=(others=>'0');
begin
with Jump select mux1<=JumpAddress when '1',
                 mux2 when others;
with PcSrc select mux2<=BranchAddress when '1',
                 sum when others;
process(clk)
begin
if rst ='1' then
    q<=x"00000000";
    elsif rising_edge(clk) then 
        if en='1' then 
            q<=mux1;
        end if;
end if;
end process;
sum<=q+4;        
NextAddress<=sum;
Instruction <= rom(conv_integer(q(6 downto 2)));
end Behavioral;