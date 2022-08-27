Note: this project is not terminated an barely commented, however the most important features work. I would fix the rest if I had time.

On April 1st, the final draw for soccer world cup 2022 (hosted in Qatar) will be held. (Or was held, depending on what day we are today)

This app that can calculate the exact probabilities of every possible scenario for the draw.

Java runtime environment required (it might already be installed on your machine). How to run the program: [Note: for now on, "Enter x" means "type x and press enter"]

 - Open terminal on your machine
 - Navigate to [location where you copied the project]/target/classes
 - enter "java init.MegaMain"

The program launches.


---------

Notes when you are asked to choose between many options:

- whenever you want to type an option, you may type only a prefix of it as long as it does not match any other option. For an example, when you want to choose the qatar2022 database, typig "qa" is enough.

- typing "exit" always allows you to close the program.

- if an option ends with an asterisk, it means at has to be entered with a number, seperated with comma. Ex: you can enter "next,8" for option "next*"

--------

First, many databases will be proposed to you, so enter the name of the database you want to use. You probably want qatar2022, others are just for testing up to now.
Then, choose between "simulator" and "exact", and "load-custom". "exact" feature (the most important feature of this program) gives you access to all exact probabilities from initial state, while "simulator" use simulation tools instead of exact probabilities. "load-custom" is broken, described far below.
--------
If you choose "exact", you may then enter "restart" to make the program recalculate all the probabilities, but since they are already summarized in a file, you may just want to reload them with "load" which is much faster.
Then, enjoy. You may enter any scenario and the program will tell you its probability. For help on the format on your input, enter "help".
--------

If you rather choose "simulator". You get a simulation tool. You have six options: undo*,current,next*,exactFromHere,simulAvg*,simulHard*

The purpose of the first three options are obvious, just play with it. simulHard and simulAvg are broken, (that's what's meant by "this project is not completed"), so don't use them. "exactFromHere" allows you to caculate the  exact probabilities, but starting from the current state rather than the initial state.

--------

"exactFromHere" also produces a custom file which you should be able to load by "load-custom" the next time you launch the program, but No! This feature is also broken.



