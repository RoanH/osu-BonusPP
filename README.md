# osu! Bonus PP

A simple tool that can be used to calculate the amount of bonus pp and ranked scores a player has (in osu!).
osu! forum post: [post](https://osu.ppy.sh/forum/t/538470)

Public GitHub repository: [link](https://github.com/RoanH/osu-BonusPP)

# Forum post

So a little while back a friend of mine asked me to write a program to calculate how much bonus PP he had.
And after writing said program I figured, why not share it with the rest of the community?

For those of you that don't know what bonus PP is, it's basically PP awarded for having passes on a certain number of maps. You can read more about this on the [wiki](https://osu.ppy.sh/wiki/Performance_Points).
![Bonus PP Table](https://i.ppy.sh/99970ce4d162a4dcb6d8afd86e1281b17ba095d2/687474703a2f2f772e7070792e73682f662f66372f4f7375626f6e757370702e706e67)

To use the program you will need an osu! API key, which you can get [here](https://osu.ppy.sh/p/api).

After you run the program you should see the following form:
![Input Form](https://i.ppy.sh/bf5a0a47bc8e637a84b0286f21b1a6e7e1b946d6/687474703a2f2f692e696d6775722e636f6d2f6d4f59626859382e706e67)

After you fill in the details you should get an output like this:
![Output Form](https://i.ppy.sh/18b9cdf3f27a90b0afe5efb567bb90912c25af8c/687474703a2f2f692e696d6775722e636f6d2f303947744379712e706e67)

It'll tell you both the amount of bonus PP you have and from that it'll also calculate the number of ranked scores/passes you have.
Every point on the graph represents one of your top 100 scores and what it's worth both weighted and raw (I just thought it looked nice so I left it in).

One thing to note however is that this program uses some statistical tricks in order to get an approximation of the amount of bonus PP you have. So the returned values are not 100% acurate, I believe them to be preatty close though :)

So have fun with the program (or not :P) and feel free to report any bugs you may encounter.

## Downloads (Java required)
- [Windows executable](https://github.com/RoanH/osu-BonusPP/raw/master/BonusPP.exe) (Windows might complain about it being an unsafe file :o )
- [Runnable Java Archive](https://github.com/RoanH/osu-BonusPP/raw/master/BonusPP.jar) (Apparently Windows doesn't complain about Runnable Jars :D )

## Implementation details
This program calculates the amount of bonus PP a player has using their top 100 scores.
First it adds the top 100 scores together and then it uses linear regression to account for pp gained from plays that are no longer in the top 100 (I actually wouldn't mind if someone checked the statistics xD The returned results look plausible though so I think everything checks out).

## History
Project development started: 27 December 2016