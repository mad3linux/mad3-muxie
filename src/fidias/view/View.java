package fidias.view;

public interface View {

	/**
	 * load the elements of the Activity.
	 * carrega os elementos de um Activity.
	 */
	public abstract void load();
	
	/**
	 * set listeners to buttons, menus, etc.
	 * configura os listeners para botões, menus, etc.
	 */
	public abstract void set();
}
