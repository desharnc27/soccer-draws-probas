On April 1st, the final draw for soccer world cup 2022 (hosted in Qatar) will be held. (Or was held, depending on what day we are today)

I programmed an app that can calculate the exact probabilities of every possible scenario for the draw.

Java runtime environment required (it might already be installed on your machine).
How to run the program:
[Note: for now on, "Enter x" means "type x and press enter"]

- Open terminal on your machine
- Navigate to [location where you copied the project]/target/classes
- enter "java init.MegaMain"

The program launches.

First, many databases will be proposed to you, so enter the name of the database you want to use. You probably want qatar2022, others are just for testing up to now.

Then, choose between "simulator" and "exact". "exact" feature gives you access to all exact probabilities, while "simulator" use simulation tools instead of exact probabilities. Up to now, "simulator" is barely implemented and only performs a single random simulation of the whole draw journey.

If you choose "exact", you may then enter "restart" to make the program recalculate all the probabilities, but since they are already summarized in a file, you may just want to reload them with "load" which is much faster.

Then, enjoy. You may enter any scenario and the program will tell you its probability. For help on the format on your input, enter "help".

Important note: if at the very beginning of the launch, a message tells you that the version is restricted (demo, hidden algorithm), it means that only few of the features described above are available. Be sure to read that message to know what features are available. If you want to check the full version and/or to know more about the algorithm behind all this, contact me: charles-.d@hotmail.com.
