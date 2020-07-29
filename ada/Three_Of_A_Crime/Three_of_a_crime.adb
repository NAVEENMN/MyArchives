with Ada.Text_IO, Ada.Integer_Text_IO;
use Ada.Text_IO, Ada.Integer_Text_IO;
WITH Ada.Numerics.Discrete_Random;

procedure main is
   type criminal is array(1..7) of integer;
   criminallist :criminal;
   guessed : criminal;
   type player is array(1..3) of integer;
   playerlist :player;

   status: Integer;
   first, second, third : Integer;
   trigger : Integer;
   numberofplayer : Integer;
   numberofperpetrator : Integer;
   perpetratorleft : Integer;
   guesstemp : Integer;

   SUBTYPE RandomRange IS Positive RANGE 1..7;
   PACKAGE Random_7 IS NEW Ada.Numerics.Discrete_Random
     (Result_Subtype => RandomRange);
   G: Random_7.Generator;

   function generaterandom(status: integer) return integer is
   begin
      loop
      criminallist(Random_7.Random(Gen => G)) := 1;
      exit when criminallist(1)+criminallist(2)+criminallist(3)+
        criminallist(4)+criminallist(5)+criminallist(6)+criminallist(7) = 3;
      end loop;
      return status;
   end;

   function choosenumberofplayer(status: integer) return Integer is
   begin
      Put_Line("Welcome to the Three of A Crime game");
      loop
         Put_Line("Enter the number of players between 1 to 3:");
         get(numberofplayer);
         exit when numberofplayer = 1 or numberofplayer = 2 or numberofplayer = 3;
      end loop;
      if numberofplayer = 1 then
         playerlist(1) := 1;
      elsif numberofplayer = 2 then
         playerlist(1) := 1; playerlist(2) := 1;
      else
         playerlist(1) := 1; playerlist(2) := 1; playerlist(3) := 1;
      end if;
      return status;
   end;

   function checkguessed(status: integer) return Integer is
   begin
      new_line;
      if(guessed(1) + guessed(2) + guessed(3) + guessed(4) + guessed(5) +
           guessed(6) + guessed(7) > 0) then
         Put("The criminals has been guessed are:");
         for i in 1..7 loop
            if(guessed(i) = 1) then
               put(i, Width => 2);
            end if;
        end loop;
         if(perpetratorleft = 3) then
            new_line;
            Put_Line("None of these are the perpetrators");
         else
            New_Line;
            Put("Among these, ");
            for i in 1..7 loop
               if(guessed(i) = 1 and criminallist(i) = 1) then
                  put(i, Width => 1);
                  put(" ");
               end if;
            end loop;
            put("are the perpetrators.");
            new_line(2);
         end if;
      end if;
      return status;
   end;

   function displayrandom(status: integer) return integer is
   begin
      Put("The random selected criminals are:");
      Put(first, Width =>2);Put(",");
         Put(second, Width =>2);Put(",");
         Put(third, Width =>2);Put(".");
         New_Line;
         Put("The number of perpetrators are:");
         Put(numberofperpetrator, Width =>2);
         New_Line;

      return status;
   end;

   function playerstatus(status: integer; playerindex: integer) return integer is
   begin
       if(playerlist(playerindex) = 1 and perpetratorleft /= 0) then
         loop
            Put("Player");
            Put(playerindex, width => 1);
            Put( ", enter the number of guess, or enter 0 to pass");
            New_Line;
            Get(guesstemp);
            exit when guesstemp >=0 and guesstemp <=7;
         end loop;
         if(guesstemp /= 0) then
            --make sure no repeated guesses.
            while guessed(guesstemp) = 1 loop
               Put_Line("Do not enter repeated guesses, please enter again");
               Get(guesstemp);
               exit when guesstemp = 0;
            end loop;
            if guesstemp /= 0 then
               guessed(guesstemp) := 1;
               if(criminallist(guesstemp) = 1) then
                  Put_Line("You made the right guess");
                  delay 1.0;
                  perpetratorleft := perpetratorleft - 1;
                  if(perpetratorleft = 0) then
                     Put_Line("You win!!");
                     Put("The pertrators are:");
                     for i in 1..7 loop
                        if(criminallist(i) = 1) then
                           Put(i, width => 2);
                        end if;
                     end loop;
                     delay 2.0;
                     trigger := 1;
                  end if;
               else
                  Put_Line("Sorry, your guess is wrong, you are out of the game");
                  playerlist(playerindex) := 0;
                  delay 1.0;
                  numberofplayer := numberofplayer - 1;
                  if numberofplayer = 0 then
                     trigger := 1;
                  end if;
               end if;
            end if;
         else
            Put_Line("Passed!");
            delay 1.0;
         end if;
       end if;
      return status;
   end;



begin
   criminallist := (0,0,0,0,0,0,0);
   guessed := (0,0,0,0,0,0,0);
   trigger := 0;
   Random_7.Reset (Gen => G);
   perpetratorleft := 3;


   status := generaterandom(1);

--     --Show actual perpetrator for testing
--     Put("The actual perpetrator are:");
--     for i in 1..7 loop
--        if(criminallist(i) = 1) then
--           Put(i, width => 2);
--        end if;
--     end loop;
--     new_line;


   --Choose number of player
   status := choosenumberofplayer(1);

   --     New_Line(32);
   Ada.Text_IO.Put(ASCII.ESC & "[2J");

   --Generating 3 random criminials
   while trigger = 0 loop
      numberofperpetrator := 0;
      first := Random_7.Random(Gen => G);
      loop
         second := Random_7.Random(Gen => G);
         exit when second /= first;
      end loop;
      loop
         third := Random_7.Random(Gen => G);
         exit when third /= first and third /= second;
      end loop;
      if criminallist(first) = 1 then
         numberofperpetrator := 1;
      end if;
      if criminallist(second) = 1 then
         numberofperpetrator := numberofperpetrator + 1;
      end if;
      if criminallist(third) = 1 then
         numberofperpetrator := numberofperpetrator + 1;
      end if;




      --no display of the right answer
      if numberofperpetrator /= 3 then
         status := displayrandom(1);

         --Game starts from player 1
         if playerlist(1) = 1 then
            status := checkguessed(1);
         end if;
         status := playerstatus(1,1);
         --Game starts from player 2
         if playerlist(2) = 1 then
            status := checkguessed(1);
         end if;
         status := playerstatus(1,2);
         --Game starts from player 3
         if playerlist(3) = 1 then
            status := checkguessed(1);
         end if;
         status := playerstatus(1,3);


      end if;--numberofperpetrator /= 3
             --        New_Line(32);
      Ada.Text_IO.Put(ASCII.ESC & "[2J");
   end loop;--generating loop




end main;
