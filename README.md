# osu! Bonus PP ![](https://img.shields.io/github/release/RoanH/osu-BonusPP.svg) ![](https://img.shields.io/github/downloads/RoanH/osu-BonusPP/total.svg)

A simple tool that can be used to calculate the amount of bonus pp and ranked scores a player has (in osu!).

# Forum post

So a little while back a friend of mine asked me to write a program to calculate how much bonus PP he had.
And after writing said program I figured, why not share it with the rest of the community?

For those of you that don't know what bonus PP is, it's basically PP awarded for having passes on a certain number of maps. You can read more about this on the [wiki](https://osu.ppy.sh/wiki/Performance_Points).    
![Bonus PP Table](https://i.ppy.sh/99970ce4d162a4dcb6d8afd86e1281b17ba095d2/687474703a2f2f772e7070792e73682f662f66372f4f7375626f6e757370702e706e67)

To use the program you will need an osu! API key, which you can get [here](https://osu.ppy.sh/p/api).

After you run the program you should see the following form:    
![Input Form](https://i.imgur.com/Ui6od74.png)

After you fill in the details you should get an output like this:    
![Output Form](https://i.imgur.com/te0XuTi.png)

It'll tell you both the amount of bonus PP you have and from that it'll also calculate the number of ranked scores/passes you have.
Every point on the graph represents one of your top 100 scores and what it's worth both weighted and raw (I just thought it looked nice so I left it in).

One thing to note however is that this program uses some statistical tricks in order to get an approximation of the amount of bonus PP you have. So the returned values are not 100% acurate, I believe them to be preatty close though :)

So have fun with the program (or not :P) and feel free to report any bugs you may encounter.

## Downloads
_Requires Java 8 or higher_
- [Windows executable](https://github.com/RoanH/osu-BonusPP/releases/download/v1.2/BonusPP-v1.2.exe)
- [Runnable Java Archive](https://github.com/RoanH/osu-BonusPP/releases/download/v1.2/BonusPP-v1.2.jar)

All releases: [releases](https://github.com/RoanH/osu-BonusPP/releases)<br>
GitHub repository: [repository](https://github.com/RoanH/osu-BonusPP)<br>
Forum post: [post](https://osu.ppy.sh/community/forums/topics/538470)

## History
Project development started: 26 December 2016
