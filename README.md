*******************************************************************

* Title:  DiceBag
* Author: [Matthew Boyette](mailto:Dyndrilliac@gmail.com)
* Date:   3/31/2012

*******************************************************************

This code makes use of my [Custom Java API](https://github.com/Dyndrilliac/java-custom-api). In order to build this source, you should clone the repository for the API using your Git client, then import the project into your IDE of choice (I prefer Eclipse), and finally modify the build path to include the API project. For more detailed instructions, see the README for the API project.

This application simulates dice rolls for table top games like Dungeons & Dragons. It accepts input in the form of a string. The string should be formatted such that it contains two positive integers separated by the character 'd'. The 'd' is not case sensitive. Additionally, it has a new combat tracking system built into it that allows it to keep track of initiative, HP, the number of rounds, etc.
	
Examples: 3d6, 2d8, 1d20, 15D6, 10D10, 4D4, etc.

A pre-compiled JAR binary can be downloaded from [this link](https://www.dropbox.com/s/mo5cm0efhcr9gmw/DiceBag.jar).

*******************************************************************

* Title:  CombatTracker
* Author: [Matthew Boyette](mailto:Dyndrilliac@gmail.com)
* Date:   2/19/2014

*******************************************************************

This class is an add-on module for DiceBag which allows a DM to track combat information like health, initiative, and the current round. It interfaces with the DiceBag class so that initiative die rolls are recorded in the log automatically. Currently only the v3.5 d20 rules are implemented but in a future version users will be able to seamlessly switch configurations.

CombatTracker can also be used to track battlefield position. The input for position tracking will require the information to be in a specific format. A link to a sample blank battle grid is provided below. It features 108 squares in a 9x12 configuration using the format "X:YY" where the Y coordinate is zero-padded so each position is a single fixed width. Coordinates begin counting at one, not zero. Only positive integers are allowed.

[Sample Blank Battle Grid](https://www.dropbox.com/s/i1gcz06x7tpyqge/Blank_Battle_Grid.pdf)

In future versions, traps will be fully supported so that moving to a particular square will trigger the trap's effects automatically. Another planned future feature is for the user to be able to enter damage and healing in more flexible formats, such us 1d6+4.

*******************************************************************

* Title:  Creature
* Author: [Matthew Boyette](mailto:Dyndrilliac@gmail.com)
* Date:   2/19/2014

*******************************************************************

This class is a common resource for the DiceBag module and its add-on modules to use. It was originally located nested within the CombatTracker class but I have separated it out to avoid duplicating code when using similar objects in other modules.

This class is an abstract class that other classes extend. This allows for alternative initiative logic, different HP thresholds, and other customized programming.

*******************************************************************

* Title:  CreatureV35
* Author: [Matthew Boyette](mailto:Dyndrilliac@gmail.com)
* Date:   4/07/2014

*******************************************************************

This class is a common resource for the DiceBag module and its add-on modules to use. It was originally located nested within the CombatTracker class but I have separated it out to avoid duplicating code when using similar objects in other modules.
	
CreatureV35 is the default Creature type in DiceBag, and is just a non-abstract version of Creature. It represents a D&D 3.5 creature.