# MHStreamAssistant - Monster Hunter Stream Assistant!

Quick start guide:

This project utilizes Java as its language. To run the bot,
you're going to need to download the Java Runtime Environment,
which can be obtained [here.](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)

Please make sure to put the bot somewhere that it can actually
have access to file reading/writing, because the bot does that for
its backup function, among other things!

PLEASE make sure to use a separate bot account with this program; don't
use your main Twitch account to login to this bot. The source code is
available here, obviously, so you can read through it if you're really
concerned about security issues, but you should still always use a bot
account for bots, to keep your own account safe.

The commands that the bot listen to are as follows:

!hire: This registers viewers into the queue. The syntax is !hire (name)
(HR), i.e. !hire Rose 211. Names can be multiple words, so you can go ham.

!skip: This allows a viewer to voluntarily skip themselves, meaning that
they won't be selected by any of the auto-hiring buttons in the hunter
table. Good for 

!unskip: This undoes the skip status for a viewer. For when they come back.

!upnext: This shows the next three people in the queue from the top.

Last note: If you're having issues with people not being able to sign up
using !hire, please make sure you've turned the bot "on" (in the options
panel).

If you have any questions, please contact me at my email (alexyle@gmail.com).