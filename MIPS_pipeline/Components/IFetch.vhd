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
--Programul contorizeaza numarul de elemente impare dintr-un vector de N elemente.
--Numarul de elemente(N) este stocat la adresa 16, iar vectorul este stocat in memorie incepand de la adresa 32.
--Elementele din vector sunt stocate consecutiv.
b"001000_00000_00001_0000000000000000",--X"2001_0000"--00  addi $1,$0,0 #i=0, contorul buclei
b"001000_00000_00010_0000000000100000",--X"2002_0020"--01  addi $2,$0,32 #initializarea indexului locatiei de memorie pentru vector
b"100011_00000_00011_0000000000010000",--X"8C03_0010"--02  lw   $3,16($0) #incarcam valoarea lui N
X"00000000",--03
X"00000000",--04
b"000000_00000_00011_00100_00000_100000",--X"0003_2020"--05  add  $4,$0,$3 #numarul maxim de iteratii=N
X"00000000",--06
b"000000_00000_00000_00101_00000_100000",--X"0000_2820"--07  add  $5,$0,$0 # rezultat=0
b"000100_00001_00100_0000000000010011",--X"1024_0013"--08  beq $1,$4,19 #sarim peste ... instructiuni daca nu sunt egale valorile
X"00000000",--09
X"00000000",--10
X"00000000",--11
b"100011_00010_00110_0000000000000000",--X"8C46_0000"--12      lw $6,0($2) #luam elementul curent din vector
b"001000_00000_01010_0000000000000001",--X"200A_0001"--13    addi $10,$0,1
X"00000000",--14
X"00000000",--15
b"000000_00110_01010_00111_00000_100100",--X"00CA_3824"--16      and $7,$6,$10 #facem and cu 1 pentru a verifica ultimul bit
X"00000000",--17
X"00000000",--18
b"000100_00111_00000_0000000000000100",--X"10E0_0004--19      beq $7,$0,4 #daca e zero, inseamna ca numarul este par  
X"00000000",--20
X"00000000",--21
X"00000000",--22
b"001000_00101_00101_0000000000000001",--X"20A5_0001"--23      addi $5,$5,1 #incrementam contorul cu 1
b"001000_00010_00010_0000000000000100",--X"2042_0004"--24      addi $2,$2,4 #indexul urmatorului element din sir
b"001000_00001_00001_0000000000000001",--X"2021_0001"--25      addi $1,$1,1 #i=i+1
b"000010_00000000000000000000001000",--X"0800_0008"----26  j 8
X"00000000",--27
b"101011_00000_00101_0000000000000000",--X"AC05_0000"--28  sw $5,0($0) #stocam la adresa 0 rezultatul
others => X"00000000");
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
NextAddress<=sum;--PC<=PC+4
Instruction <= rom(conv_integer(q(6 downto 2)));--Luam instructiunea din memoria ROM
end Behavioral;