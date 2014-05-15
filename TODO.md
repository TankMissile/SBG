<ul>
  <li> Sounds </li>
  <li> Music </li>
  <li> Player attacks (Thinking of using mouse, since right hand is currently unused) </li>
  <li> Player death </li>
  <li> Lives? </li>
  <li> Game Over screen </li>
  <li> Main Menu </li>
  <li> Level Editor </li>
  <li> Level exits/transitions </li>
  <li> Enemies </li>
  <li> Lava/acid </li>
  <li> Rotate spikes to face away from wall </li>
  <li> Better hit detection </li>
  <li> Save/Load </li>
  <li> Moving platforms </li>
  <li> Better fluid mechanics (player movement) </li>
  <li> More particles (double jump, hit wall, running, wall jump, water splash) </li>
  <li> Breath meter (underwater) </li>
  <li> Optimize particle, entity animation (Right now each one creates its own thread, slows down really fast as you add more </li>
  <li> Animate top row of fluids </li>
  <li> Reduce draw time for tiles, maybe preload an array of all possible tile types and select one for each tile, rather than reloading it for each tile </li>
  <li> Functional pause menu (it exists, but the buttons don't do anything) </li>
  <li> Nice-looking pause menu </li>
  <li> more wall/background types </li>
  <li> Conveyor belts </li>
  <li> Health restoratives </li>
  <li> Foregrounds (i.e. a front wall to the house, which becomes transparent when you walk behind it, and nearby tiles) </li>
  <li> Decorations (torches, cobwebs, clouds, etc.) </li>
  <li> Whatever else you think would be cool </li>
</ul>

If you can't get the graphics to fit the theme, don't worry about it, I can retexture them when I get back

Determine cause of bugs (and maybe fix them): 
<ol> 
  <li> Player sometimes has spasm when landing on corner of block (might have to do with it trying to decide where the bounds are?)</li>
  <li> Player sometimes doesn't resize properly when using superjump (I can fix it by forcing a resize if it's wrong every frame, but that's a lame way to "fix" a bug) </li>
  <li> Player sometimes shoots off to the left when using superjump (maybe related to #2)</li>
  <li> Player sometimes shoots off to the left while holding spacebar in cramped spaces (maybe related to #2) </li>
  <li> Player sometimes lands above his actual boundary when using air drop </li>
</ol>
