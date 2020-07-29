with Ada.Exceptions;
with Ada.Calendar; use Ada.Calendar;
with Ada.Calendar.Formatting; use Ada.Calendar.Formatting;
with Ada.Calendar.Time_Zones; use Ada.Calendar.Time_Zones;
with Fifo;
with Ada.Text_Io; use Ada.Text_Io;
 
procedure Mclock is
   	package Int_Fifo is new Fifo(Integer);
   	use Int_Fifo;
	-- Declarations
	-- FIFO Declarations
  	Tray_one_min : Fifo_Type;-- first tray size 4
  	Tray_five_min : Fifo_Type;-- scond tray size 2
   	Tray_fifteen_min : Fifo_Type;-- third tray size 3
 	Tray_one_hour : Fifo_Type;-- fourth tray size 11
   	Tray_collector : Fifo_Type;-- this is a reserve tray size 21 (4+2+3+11+1(trigger))
  	-- temporary variables
	Val : Integer;
	ptemp : Integer;
	H : Integer;
	l : Integer;
	-- size variables
	size_of_one_min : Integer;
	size_of_five_min : Integer;
	size_of_fifteen_min : Integer;
	size_of_one_hour : Integer;
	size_of_collector : Integer;
	-- size variables
	max_size_of_one_min : Integer;
	max_size_of_five_min : Integer;
	max_size_of_fifteen_min : Integer;
	max_size_of_one_hour : Integer;
	max_size_of_collector : Integer;
	Now : Time := Clock;
	-- functions
	function push_to_one_min(status: Integer) return Integer is
	begin
		Pop(Tray_collector, Val);--when one min tray is full we have not yet poped collector
		Push(Tray_one_min,Val);
		size_of_one_min := size_of_one_min + 1;
		size_of_collector := size_of_collector - 1;
		return status;
	end;

	function push_to_five_min(status: Integer) return Integer is
	begin
		Pop(Tray_collector, Val);
		Push(Tray_five_min, Val);
		size_of_collector := size_of_collector - 1;
		size_of_five_min := size_of_five_min + 1;
		for I in 1..4 loop -- reset the minute tray
			Pop(Tray_one_min, Val);
			Push(Tray_collector, Val);
			size_of_one_min := size_of_one_min - 1;
			size_of_collector := size_of_collector + 1;
		end loop;
	return status;
	end;

	function push_to_fifteen_min(status: Integer) return Integer is
	begin
		Pop(Tray_collector, Val);
		Push(Tray_fifteen_min, Val);
		size_of_collector := size_of_collector - 1;
		size_of_fifteen_min := size_of_fifteen_min + 1;
		-- we have to reset both five min and one min tray
		-- reseting five min tray
		for I in 1..2 loop -- reset the five tray
			Pop(Tray_five_min, Val);
			Push(Tray_collector, Val);
			size_of_five_min := size_of_five_min - 1;
			size_of_collector := size_of_collector + 1;
		end loop;
		-- reseting one min tray
		for I in 1..4 loop -- reset the minute tray
			Pop(Tray_one_min, Val);
			Push(Tray_collector, Val);
			size_of_one_min := size_of_one_min - 1;
			size_of_collector := size_of_collector + 1;
		end loop;
	return status;
	end;

	function push_to_one_hour(status: Integer) return Integer is
	begin
		Pop(Tray_collector, Val);
		Push(Tray_one_hour, Val);
		size_of_collector := size_of_collector - 1;
		size_of_one_hour := size_of_one_hour + 1;
		-- we have to reset one min, five min and fifteen minute trays
		-- reseting one min tray
		for I in 1..4 loop -- reset the minute tray
			Pop(Tray_one_min, Val);
			Push(Tray_collector, Val);
			size_of_one_min := size_of_one_min - 1;
			size_of_collector := size_of_collector + 1;
		end loop;

		-- reseting five min tray
		for I in 1..2 loop -- reset the five tray
			Pop(Tray_five_min, Val);
			Push(Tray_collector, Val);
			size_of_five_min := size_of_five_min - 1;
			size_of_collector := size_of_collector + 1;
		end loop;

		-- reset the fifteen minute tray
		for I in 1..3 loop 
			Pop(Tray_fifteen_min, Val);
			Push(Tray_collector, Val);
			size_of_fifteen_min := size_of_fifteen_min - 1;
			size_of_collector := size_of_collector + 1;
		end loop;
	return status;
	end;

	function resetAll(status: Integer) return Integer is
	begin		
		-- we have to reset one min, five min, fifteen minute and one hour trays
		-- reseting one min tray
		for I in 1..4 loop -- reset the minute tray
			Pop(Tray_one_min, Val);
			Push(Tray_collector, Val);
			size_of_one_min := size_of_one_min - 1;
			size_of_collector := size_of_collector + 1;
		end loop;

		-- reseting five min tray
		for I in 1..2 loop -- reset the five tray
			Pop(Tray_five_min, Val);
			Push(Tray_collector, Val);
			size_of_five_min := size_of_five_min - 1;
			size_of_collector := size_of_collector + 1;
		end loop;

		-- reset the fifteen minute tray
		for I in 1..3 loop 
			Pop(Tray_fifteen_min, Val);
			Push(Tray_collector, Val);
			size_of_fifteen_min := size_of_fifteen_min - 1;
			size_of_collector := size_of_collector + 1;
		end loop;

		-- reset the one hour tray
		for I in 1..11 loop 
			Pop(Tray_one_hour, Val);
			Push(Tray_collector, Val);
			size_of_one_hour := size_of_one_hour - 1;
			size_of_collector := size_of_collector + 1;
		end loop;

	return status;
	end;

	-- Initial tray setup start -----------------------------------
	function initial_tray_setup(status: Integer) return Integer is
	begin
		for I in 1..21 loop
			Push(Tray_collector, 0);
		end loop;
		return status;
	end;
	-- Initial tray setup end -------------------------------------

	-- Display function start -------------------------------------
	function print_trays(status: Integer) return Integer is
	begin
		-------------
		--tray one min
		Put("Tray A (one min):     ");
		for X in 1..size_of_one_min loop
      			Pop(Tray_one_min, ptemp);
      			Put(Integer'Image(ptemp));
			Push(Tray_one_min, ptemp);
   		end loop;
		New_Line;
		--tray five min
		Put("Tray B (five min):    ");	
		for X in 1..size_of_five_min loop
      			Pop(Tray_five_min, Val);
      			Put(Integer'Image(Val));
			Push(Tray_five_min, Val);
   		end loop;
		New_Line;
		--tray fifteen min
		Put("Tray C (fifteen min): ");
		for X in 1..size_of_fifteen_min loop
      			Pop(Tray_fifteen_min, Val);
      			Put(Integer'Image(Val));
			Push(Tray_fifteen_min, Val);
   		end loop;
		New_Line;
		--tray one hour
		Put("Tray D (One Hour):    ");
		for X in 1..size_of_one_hour loop
      			Pop(Tray_one_hour, Val);
      			Put(Integer'Image(Val));
			Push(Tray_one_hour, Val);
   		end loop;
		New_Line;
		--tray collector
		Put("Tray E (Collector):   ");
		for X in 1..size_of_collector loop
      			Pop(Tray_collector, Val);
      			Put(Integer'Image(Val));
			Push(Tray_collector, Val);
   		end loop;
		return status;
		exception
			when Constraint_Error =>
				Put("some error");
				return status;
	end;
	-- Display function end -----------------------------------------
	-- clear screen function start here
	function clear_screen(status: Integer) return Integer is
	begin
		Ada.Text_IO.Put(ASCII.ESC & "[2J");
		return status;
	end;
	-- clear screen function end
begin	-- main here
	H := initial_tray_setup(1);
	------ setup sizes
	size_of_one_min := 0;
	size_of_five_min := 0;
	size_of_fifteen_min := 0;
	size_of_one_hour := 0;
	size_of_collector := 21;
	-- size variables
	max_size_of_one_min := 4;
	max_size_of_five_min := 2;
	max_size_of_fifteen_min := 3;
	max_size_of_one_hour := 11;
	max_size_of_collector := 21;
	--------------
	H := clear_screen(1);
	l := 0;
	for I in 1..800 loop
		Put_line(Image(Date => Now, Time_Zone => -7*60));
		H := print_trays(1);
		delay Duration(0.5);--print and wait for a sec
		H := clear_screen(1);
		delay Duration(0.05);--clear and wait for a sec
		if size_of_one_min < max_size_of_one_min then
			H := push_to_one_min(1);-- size of one min inc and collector dec
			elsif size_of_five_min < max_size_of_five_min then
				H := push_to_five_min(1);
				elsif size_of_fifteen_min < max_size_of_fifteen_min then
					H := push_to_fifteen_min(1);
					elsif size_of_one_hour < max_size_of_one_hour then
						H := push_to_one_hour(1);
						else
							H := resetAll(1);
		end if;
		New_Line;
	end loop;
end Mclock;
