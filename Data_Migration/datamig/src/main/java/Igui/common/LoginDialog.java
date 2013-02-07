package Igui.common;

//package Igui.Common;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

/**
 * Utility class used for login dialogs.
 * <p>
 * The dialog asks the user to provide authentication credentials (i.e., user
 * name and password). The implementing class has to override the
 * {@link #authenticate(String, String)} method in order to provide the
 * authentication mechanism. In case of authentication failure the
 * {@link #authenticate(String, String)} shall throw an exception; if not then
 * it is assumed that the authentication has been successful.
 * <p>
 * The {@link #authenticate(String, String)} method is executed in a
 * {@link SwingWorker}, so it is safe to perform remote blocking calls.
 * <p>
 * Following the Swing threading model, an object of this class has to be used
 * in the EDT (including its constructor). A few methods are thread safe:
 * {@link #getUserName()} and {@link #getPassword()}.
 */

public abstract class LoginDialog {
	// Keep them volatile so that the references can be passed safely between
	// threads
	private volatile String userName = "";
	private volatile String pswd = "";

	private final JLabel userNameLabel = new JLabel("User Name");
	private final JTextField userNameField = new JTextField(this.userName);
	private final JLabel passLabel = new JLabel("User Password");
	private final JPasswordField passField = new JPasswordField(this.pswd);
	private final JButton okButton = new JButton("OK");
	private final JButton cancelButton = new JButton("Cancel");
	private final JOptionPane optPane;
	// This is not final: the dialog is recreated when currentAuthUserLabel
	// changes,
	// otherwise the layout breaks
	private JDialog optDialog;
	private final Component parentComponent;
	private final String dialogTitle;

	// The SwingWorker is used to check the user authentication
	private class AuthenticationWorker extends SwingWorker<Void, Void> {
		private final String uname;
		private final String upass;

		AuthenticationWorker(final String uname, final String upass) {
			this.uname = uname;
			this.upass = upass;
		}

		@Override
		protected Void doInBackground() throws Exception {
			// Disable the OK button and set the cursor busy
			javax.swing.SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					LoginDialog.this.okButton.setEnabled(false);
					LoginDialog.this.optDialog.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				}
			});

			// Here call the authentication method
			LoginDialog.this.authenticate(this.uname, this.upass);
			return null;
		}

		@Override
		protected void done() {
			try {
				// Get the result, save the info about the authenticated user
				// and then hide the dialog
				// The dialog is not hidden if the authentication fails (the
				// user may want to retry)
				this.get();
				LoginDialog.this.saveInfo();
				LoginDialog.this.hide();
			} catch (final InterruptedException ex) {
				Thread.currentThread().interrupt();
				final String msg = "Cannot get the authentication result because the EDT thread has been interuppted!";
				final ErrorInfo errorInfo = new ErrorInfo("Authentication Error", msg, null, null, ex, null, null);
				JXErrorPane.showDialog(LoginDialog.this.parentComponent, errorInfo);
				Thread.currentThread().interrupt();
			} catch (final ExecutionException ex) {
				final String msg = "Authentication failed for user " + this.uname + ". Reason: " + ex.getCause();
				final ErrorInfo errorInfo = new ErrorInfo("Authentication Error", msg, null, null, ex, null, null);
				JXErrorPane.showDialog(LoginDialog.this.parentComponent, errorInfo);
			} finally {
				// Queue to the EDT to be sure that this is correctly executed
				// after the cursor
				// has been put in the wait state. Yes, we already are in the
				// EDT but there is
				// a Swing bug report about this.
				javax.swing.SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						LoginDialog.this.okButton.setEnabled(true);
						LoginDialog.this.optDialog.setCursor(Cursor.getDefaultCursor());
					}
				});
			}
		}
	}

	/**
	 * Constructor.
	 * <p>
	 * TO be executed in the EDT!
	 * 
	 * @param parent
	 *            The parent component of the dialog
	 * @param title
	 *            The dialog title
	 */
	public LoginDialog(final Component parent, final String title) {
		assert (javax.swing.SwingUtilities.isEventDispatchThread() == true) : "EDT violation!";

		final Object[] msg = new Object[] { this.userNameLabel, this.userNameField, this.passLabel, this.passField};
		final Object[] opt = new Object[] { this.okButton, this.cancelButton };
		this.optPane = new JOptionPane(msg, JOptionPane.QUESTION_MESSAGE);
		this.optPane.setOptions(opt);
		this.parentComponent = parent;
		this.dialogTitle = title;

		// Initialize the dialog anyway to not have a null reference around
		this.optDialog = this.getDialog(parent, title);

		// When the OK button is pressed. the SwingWorker is executed to start
		// the authentication
		this.okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				new AuthenticationWorker(LoginDialog.this.userNameField.getText(), String.valueOf(LoginDialog.this.passField.getPassword()))
						.execute();
			}
		});

		// The CANCEL button just hides the dialog
		this.cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(final ActionEvent e) {
				LoginDialog.this.hide();
			}
		});
	}

	/**
	 * It makes the dialog visible.
	 * <p>
	 * Since the dialog is modal, this method blocks. To be executed in the EDT!
	 */
	public void show() {
		assert (javax.swing.SwingUtilities.isEventDispatchThread() == true) : "EDT violation!";
		this.optDialog = this.getDialog(this.parentComponent, this.dialogTitle);
		this.optDialog.setVisible(true);
	}

	/**
	 * It hides the dialog.
	 * <p>
	 * To be executed in the EDT!
	 */
	public void hide() {
		assert (javax.swing.SwingUtilities.isEventDispatchThread() == true) : "EDT violation!";

		this.optDialog.setVisible(false);
		this.optDialog.dispose();
	}

	/**
	 * It returns the password of the last authenticated user.
	 * <p>
	 * Thread safe method.
	 * 
	 * @return The password of the last authenticated user
	 */
	public String getPassword() {
		return this.pswd;
	}

	/**
	 * It returns the name of the last authenticated user.
	 * <p>
	 * Thread safe method.
	 * 
	 * @return The name of the last authenticated user
	 */
	public String getUserName() {
		return this.userName;
	}

	/**
	 * Method to be overridden and implementing the authentication procedure. It
	 * shall throw an exception if the authentication fails.
	 * 
	 * @param user
	 *            The user name
	 * @param passWord
	 *            The user password
	 * @throws Exception
	 *             If an exception is thrown then it means that the
	 *             authentication failed.
	 */
	protected abstract void authenticate(final String user, final String passWord) throws Exception;

	/**
	 * It creates the dialog to show.
	 * <p>
	 * Use this method any time the {@link #currentAuthUserLabel} changes.
	 * 
	 * @param parent
	 *            The dialog parent component
	 * @param title
	 *            The dialog title
	 * @return The dialog containing the option pane
	 */
	private JDialog getDialog(final Component parent, final String title) {
		assert (javax.swing.SwingUtilities.isEventDispatchThread() == true) : "EDT violation!";

		final JDialog d = this.optPane.createDialog(parent, title);
		d.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		return d;
	}

	/**
	 * It saves the information about the authenticated user.
	 * <p>
	 * This should be called only when the user authentication succeeds.
	 */
	private void saveInfo() {
		assert (javax.swing.SwingUtilities.isEventDispatchThread() == true) : "EDT violation!";

		this.userName = this.userNameField.getText();
		this.pswd = String.valueOf(this.passField.getPassword());
	}

	public static void main(final String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final LoginDialog ld = new LoginDialog(null, "TITLE") {
					public void authenticate(String usr, String pwd) throws Exception {

					}
				};
				ld.show();
				System.exit(0);
			}
		});
	}
}