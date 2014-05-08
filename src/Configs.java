
public class Configs {
	private static int[] keys;
	private static int[] altkeys;
	/****************************
	 * Keys array values:       *
	 * 0: left                  *
	 * 1: right                 *
	 * 2: crouch                *
	 * 3: jump                  *
	 * 4: action                *
	 * 5: menu                  *
	 ****************************/
	
	public static void init(){
		keys = new int[10];
		altkeys = new int[10];
		
		keys[0] = 0x41;
		keys[1] = 0x44;
		keys[2] = 0x53;
		keys[3] = 0x57;
		keys[4] = 0x20;
		keys[5] = 0x1B;
		
		//Set all alternate keybinds to -1
		for(int i = 0; i < 6; i++){
			altkeys[i] = -1;
		}
		
		//make space the alternate jump key
		altkeys[3] = 0x20;
	}
	
	public static boolean isLeftKey(int key){
		if (key == keys[0] || key == altkeys[0])
			return true;
		
		return false;
	}
	public static boolean isRightKey(int key){
		if (key == keys[1] || key == altkeys[1])
			return true;
		
		return false;
	}
	public static boolean isCrouchKey(int key){
		if(key == keys[2] || key == altkeys[2])
			return true;
		
		return false;
	}
	public static boolean isJumpKey(int key){
		if (key == keys[3] || key == altkeys[3])
			return true;
		
		return false;
	}
	public static boolean isActionKey(int key){
		if (key == keys[4] || key == altkeys[4])
			return true;
		
		return false;
	}
	public static boolean isMenuKey(int key){
		if (key == keys[5] || key == altkeys[5])
			return true;
		
		return false;
	}
}
