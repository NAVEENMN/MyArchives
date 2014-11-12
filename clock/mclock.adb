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
  	-- flags
	Tray_one_min_status : Boolean;-- 0 empty 1 mean full
	Tray_five_min_status: Boolean;-- 0 empty 1 means full
	Tray_fifteen_min_status : Boolean;-- 0 means empty 1 means full
	Tray_one_hour_status : Boolean;-- 0 means empty 1 means full
	Tray_collector_status : Boolean;-- 
	-- temporary variables
	Val : Integer;
	Temp : Integer;
	ptemp : Integer;
	H : Integer;
	l : Integer;
	-- size variables
	size_of_one_min : Integer;
	size_of_five_min : Integer;
	size_of_fifteen_min : Integer;
	size_of_one_hour : Integer;
	size_of_collector : Integer;
	Now : Time := Clock;
	-- functions
	function push_to_one_min(status: Integer) return Integer is
	begin
		Pop(Tray_collector, Val);
		Push(Tray_one_min,Val);
		Tray_one_min_status := TRUE;
		size_of_one_min := size_of_one_min + 1;
		size_of_collector := size_of_collector - 1;
		return status;
	end;
	function push_to_five_min(status: Integer) return Integer is
	begin
		--pop from min list and push here and pop a null from here and push to 
		--collector
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
		if Tray_one_min_status then
			for X in 1..size_of_one_min loop
      				Pop(Tray_one_min, ptemp);
      				Put(Integer'Image(ptemp));
				Push(Tray_one_min, ptemp);
   			end loop;
		end if;
		New_Line;
		--tray five min
		Put("Tray B (five min):    ");
		if Tray_five_min_status then	
			for X in 1..size_of_five_min loop
      				Pop(Tray_five_min, Val);
      				Put(Integer'Image(Val));
				Push(Tray_five_min, Val);
   			end loop;
		end if;
		New_Line;
		--tray fifteen min
		Put("Tray C (fifteen min): ");
		if Tray_fifteen_min_status then
			for X in 1..size_of_fifteen_min loop
      				Pop(Tray_fifteen_min, Val);
      				Put(Integer'Image(Val));
				Push(Tray_fifteen_min, Val);
   			end loop;
		end if;
		New_Line;
		--tray one hour
		Put("Tray D (One Hour):    ");
		if Tray_one_hour_status then
			for X in 1..size_of_one_hour loop
      				Pop(Tray_one_hour, Val);
      				Put(Integer'Image(Val));
				Push(Tray_one_hour, Val);
   			end loop;
		end if;
		New_Line;
		--tray collector
		Put("Tray E (Collector):   ");
		if Tray_collector_status then
			for X in 1..size_of_collector loop
      				Pop(Tray_collector, Val);
      				Put(Integer'Image(Val));
				Push(Tray_collector, Val);
   			end loop;
		end if;
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
	-------------
	Tray_one_min_status := FALSE;-- 0 empty 1 mean full
	Tray_five_min_status:= FALSE;-- 0 empty 1 means full
	Tray_fifteen_min_status := FALSE;-- 0 means empty 1 means full
	Tray_one_hour_status := FALSE;-- 0 means empty 1 means full
	Tray_collector_status := TRUE; 
	--------------
	H := clear_screen(1);
	l := 0;
	for I in 1..4 loop
		Put_line(Image(Date => Now, Time_Zone => -7*60));
		H := print_trays(1);
		delay Duration(1.0);--print and wait for a sec
		H := clear_screen(1);
		delay Duration(1.0);--clear and wait for a sec
		H := push_to_one_min(1);
		New_Line;
	end loop;
end Mclock;
