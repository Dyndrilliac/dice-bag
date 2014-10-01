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

This class is an add-on module for DiceBag which allows a DM to track combat information like health, initiative, and the current round. It interfaces with the DiceBag class so that initiative die rolls are recorded in the log automatically. Currently only the v3.5 d20 rules are implemented but in a future version users will be able to seamlessly switch configurations. Saving and loading combatants is also an option, as is only resetting characters or monsters if desired.

CombatTracker can also be used to track battlefield position. The input for position tracking will require the information to be in a specific format. A link to a sample blank battle grid is provided below. It features 770 squares in a 22x35 configuration using the format "XX:YY" where the coordinates are zero-padded so each position is a single fixed width. Coordinates begin counting at one, not zero. Only positive integers are allowed.

[Sample Blank Battle Grid](https://www.dropbox.com/s/rszsx431xq87tz2/Blank_Battle_Grid.pdf)

In future versions, traps will be fully supported so that moving to a particular square will trigger the trap's effects automatically. Another planned future feature is for the user to be able to enter damage and healing in more flexible formats, such as '1d6+4'.

*******************************************************************

* Title:  PointBuyCalculator
* Author: [Matthew Boyette](mailto:Dyndrilliac@gmail.com)
* Date:   4/9/2014

*******************************************************************

This simple module calculates ability score distributions using a point buy system.

*******************************************************************

* Title:  EncounterCalculator
* Author: [Matthew Boyette](mailto:Dyndrilliac@gmail.com)
* Date:   4/10/2014

*******************************************************************

This simple module calculates encounter levels, encounter difficulty, standard XP rewards, and standard treasure rewards based on the number of enemies, number of players, the challenge ratings of each enemy, and the effective character level of each player.

*******************************************************************

* Title:  Creature
* Author: [Matthew Boyette](mailto:Dyndrilliac@gmail.com)
* Date:   2/19/2014

*******************************************************************

This class is a common resource for the DiceBag module and its add-on modules to use. It was originally nested within the CombatTracker class but I have since separated it out to avoid duplicating code when using similar objects in other modules. This class is an abstract class that other classes are meant to inherit and extend.

*******************************************************************

* Title:  Creature35E
* Author: [Matthew Boyette](mailto:Dyndrilliac@gmail.com)
* Date:   4/7/2014

*******************************************************************

This class is a common resource for the DiceBag module and its add-on modules to use. Creature35E is the default Creature type in DiceBag. It represents a standard D&D 3.5E creature.

*******************************************************************

* Title:  StatBlock
* Author: [Matthew Boyette](mailto:Dyndrilliac@gmail.com)
* Date:   4/9/2014

*******************************************************************

This class is a common resource for the DiceBag module and its add-on modules to use. It is a companion for the Creature class. It represents the stat block for a creature.

*******************************************************************

* Title:  StatBlock35E
* Author: [Matthew Boyette](mailto:Dyndrilliac@gmail.com)
* Date:   4/9/2014

*******************************************************************

This class is a common resource for the DiceBag module and its add-on modules to use. It is a companion for the Creature35E class. It represents the stat block for a standard D&D 3.5E creature.

*******************************************************************

* Title:  Constants
* Author: [Matthew Boyette](mailto:Dyndrilliac@gmail.com)
* Date:   4/9/2014

*******************************************************************

This class is a common resource for the DiceBag module and its add-on modules to use. It is a companion for the Creature class. It represents constants useful to a creature.

*******************************************************************

* Title:  Constants35E
* Author: [Matthew Boyette](mailto:Dyndrilliac@gmail.com)
* Date:   4/9/2014

*******************************************************************

This class is a common resource for the DiceBag module and its add-on modules to use. It is a companion for the Creature35E class. It represents constants useful to a standard D&D 3.5E creature.