Note: this project is not terminated and barely commented. However the most important features work. I would fix the rest if I had time.

On April 1st, the final draw for soccer world cup 2022 (hosted in Qatar) will be held. (Or was held, depending on what day we are today)

This app that can calculate the exact probabilities of every possible scenario for the draw. It's the main use of this program and it is working properly.

This app can also be used to simulate a draw step-by-step. It mostly works but some subfeatures of it don't yet.

This app does not only work for World Cup 2022, but also for any similar format to it. More precisely: format with pots and continental constraints.

To launch the program, open terminal, cd your way to the project folder, then into `/target/classes`, then enter:

```
java init.RealMain
```


---

First, many databases will be proposed to you, so enter the name of the database you want to use. Each database is associated with one specific edtion of a tournament. You probably want qatar2022 or conmebol2024, others are just for testing up to now.


Then, choose between "simulator" and "exact", and "load-custom". "exact" feature (the most important feature of this program) gives you access to all exact probabilities from initial state, while "simulator" use simulation tools instead of exact probabilities. "load-custom" is broken, described far below.

---

Notes when you are asked to choose between many options:

- whenever you want to type an option, you may type only a prefix of it as long as it does not match any other option. For an example, when you want to choose the qatar2022 database, typing "qa" is enough.

- typing "exit" always allows you to close the program.

- if an option ends with an asterisk, it means at has to be entered with a number, separated with comma. Ex: you can enter "next,8" for option "next*"

---

If you choose "exact" (that's the main feature of the program), you may then enter "restart" to make the program recalculate all the probabilities, but since they are already summarized in a file, you may just want to reload them with "load" which is much faster.
Then, enjoy. You may enter any scenario and the program will tell you its probability. For help on the format on your input, enter "help".

---

If you rather choose "simulator". You get a simulation tool. You have six options: undo*,current,next*,exactFromHere,simulAvg*,simulHard*

The purpose of the first three options are obvious, just play with it. simulHard and simulAvg are broken, (that's what's meant by "this project is not completed"), so don't use them. "exactFromHere" allows you to calculate the  exact probabilities, but starting from the current state rather than the initial state.

"exactFromHere" also produces a custom file which you should be able to load by "load-custom" the next time you launch the program, but No! This subfeature is also broken.