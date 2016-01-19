# Titansoft_ScheduleProgram_Evolutionary
Simple Task using Evolutionary Computation to find most fitting timetable, given a list of staff and their days off.

The basic requirements are as such:
- Each day must have 1 male and 1 female worker
- When workers are assigned onto their off days, they become unhappy

Other requirements are stated as program input parameters below.

Run the program with the following program parameters (in order)
```<weekdaySlots> <weekendSlots> <hiringLimit>```

Where
- `weekdaySlots` refers to the number of required workers on weekdays
- `weekendSlots` refers to the number of required workers on weekends
- `hiringLimit` refers to the maximum number of workers that can be hired.

------
The program runs by randomly generating a single solution instance based on the input parameters. An ideal solution is then found by mutating the instance using various procedures to "fine-tune" the solution to have as few "unhappy" workers-on-shift as possible.
