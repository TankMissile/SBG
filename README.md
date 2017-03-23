SBG
===

A Simple Box Game


You play as a smiling blue guy.<br />
<img src="https://github.com/TankMissile/SBG/blob/master/res/img/gameicon.png?raw=true" />

<b>(Default) Controls:</b><br />
<table>
  <tr>
    <th>
      Name
    </th>
    <th>
      Usage
    </th>
  </tr>
  <tr>
    <td>
      Jump
    </td>
    <td>
      W, Space
    </td>
  </tr>
  <tr>
    <td>
      Move left/right
    </td>
    <td>
      A/D
    </td>
  </tr>
  <tr>
    <td>
      Crouch
    </td>
    <td>
      S
    </td>
  </tr>
  <tr>
    <td>
      Pause
   </td>
   <td>
      Esc
    </td>
  </tr>
</table>

<b>Abilities:</b><br />
<table>
  <tr>
    <th> Ability name </th>
    <th> Usage </th>
  </tr>
  <tr>
    <td>
      Double Jump
    </td>
    <td>
      jump (airborne)
    </td>
  </tr>
  <tr>
    <td>
      Wall Run
    </td>
    <td>
      move into wall (airborne + moving up)
    </td>
  </tr>
  <tr>
    <td>
      Wall Climb
    </td>
    <td>
      Run off the top of a wall (Wall Run)
    </td>
  </tr>
  <tr>
    <td>
      Wall Slide
    </td>
    <td>
      move into wall (airborne + not moving up)
    </td>
  </tr>
  <tr>
    <td>
      Wall Jump
    </td>
    <td>
      jump (wall run, wall slide)
    </td>
  </tr>
  <tr>
    <td>
      Super Jump
    </td>
    <td>
      crouch + jump
    </td>
  </tr>
  <tr>
    <td>
      Air Drop
    </td>
    <td>
      crouch + jump (airborne)
    </td>
  </tr>
</table>


<b>Block Types</b><br />
<table>
<tr>
  <th width=200px>
    Type
  </th>
  <th>
    Description
  </th>
</tr>
<tr>
  <td>
    Stone, Wood, Dirt<br />
    <span style="margin-right:5px"> <img src="https://github.com/TankMissile/SBG/blob/master/res/img/stoneIcon.png?raw=true"></span>
    <span style="margin-right:5px"> <img src="https://github.com/TankMissile/SBG/blob/master/res/img/woodIcon.png?raw=true"></span>
    <span style="margin-right:5px"> <img src="https://github.com/TankMissile/SBG/blob/master/res/img/dirtIcon.png?raw=true"></span>
  </td>
  <td>
    Just a tile.  Unremarkable.
  </td>
</tr>
<tr>
  <td>
    Eye<br />
    <span style="margin-right:5px"> <img src="https://github.com/TankMissile/SBG/blob/master/res/img/lefteyeIcon.png?raw=true"></span>
    <span style="margin-right:5px"> <img src="https://github.com/TankMissile/SBG/blob/master/res/img/righteyeIcon.png?raw=true"></span>
  </td>
  <td>
    Eyes don't like being touched.  You won't be able to interact with the wall, but you can wall climb over the top if you've got the momentum.
  </td>
</tr>
<tr>
  <td>
    Wooden Platform<br />
    <span style="margin-right:5px"> <img src="https://github.com/TankMissile/SBG/blob/master/res/img/platformIcon.png?raw=true"></span>
  </td>
  <td>
    These allow you to drop through them if you hold the crouch button for a short duration.  Air drop will automatically ignore wooden platforms, so be careful!
  </td>
</tr>
</table>

<b>Bad things:</b>
<table>
<tr>
  <th width="200px">
    Name
  </th>
  <th>
    Activity
  </th>
</tr>
<tr>
  <td>
    Spike Trap<br />
    <img src="https://github.com/TankMissile/SBG/blob/master/res/img/spike.png?raw=true">
  </td>
  <td>
    Stationary - They just sit and wait for some poor idiot to chance upon them.  Lord have mercy on they that do.
  </td>
</tr>
<tr>
  <td>
    Sinister Bad Guy<br />
    <span style="margin-right:5px"> <img src="https://github.com/TankMissile/SBG/blob/master/res/img/mr_chief.png?raw=true"></span>
    <img src="https://github.com/TankMissile/SBG/blob/master/res/img/mr_chief_angry.png?raw=true">
  </td>
  <td>
    Always up to no good, this dastardly fellow may try anything to stop you on your quest!
  </td>
</tr>
</table>
