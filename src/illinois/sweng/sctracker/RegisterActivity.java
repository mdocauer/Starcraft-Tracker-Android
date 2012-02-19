package illinois.sweng.sctracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends Activity implements IDelegate{
	
	private static final String TAG = "sc2TrackerRegisterActivity";
	private static final int DIALOG_INVALID_EMAIL_ID = 1;
	private static final int DIALOG_INVALID_PASSWORD_ID = 2;
	
	private Button mCreateAccountButton;
	private EditText mEmail, mPassword, mPasswordConfirm;
	private ServerCommunicator mServerCommunicator;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        
        mCreateAccountButton = (Button) findViewById(R.id.registerCreateButton);
        mEmail = (EditText) findViewById(R.id.emailEditText);
        mPassword = (EditText) findViewById(R.id.passwordTextEdit);
        mPasswordConfirm = (EditText) findViewById(R.id.passwordConfirmTextEdit);
        mServerCommunicator = new ServerCommunicator(this);
        
        mCreateAccountButton.setOnClickListener(new CreateAccountHandler());
	}

	/**
	 * Builds the alert dialog for improperly entered text
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		alertDialogBuilder.setCancelable(false);
		alertDialogBuilder.setPositiveButton("Ok", new CloseDialogListener());
		
		Dialog dialog = alertDialogBuilder.create();
		
		manageDialog(id, dialog);
		
		return dialog;
	}
	
	/**
	 * Process the message to be displayed on the Dialog based on entered text
	 */
	@Override
	protected void onPrepareDialog(int id, Dialog dialog) {
		manageDialog(id, dialog);
	}
	
	/**
	 * Process the message to be displayed on the Dialog based on entered text
	 * @param id ID of the Dialog
	 * @param dialog Dialog being opened
	 */
	private void manageDialog(int id, Dialog dialog) {
		clearInputFields();
		AlertDialog alertDialog = (AlertDialog) dialog;
		CharSequence message;
		
		switch (id) {
		case DIALOG_INVALID_EMAIL_ID:
			message = getResources().getText(R.string.registerInvalidEmailMessage);
			break;
		case DIALOG_INVALID_PASSWORD_ID:
			message = getResources().getText(R.string.registerNonmatchingPasswords);
			break;
		default:
			message = "Please reenter your information";
		}
		
		alertDialog.setMessage(message);
	}
	
	/**
	 * Validate the user-entered data fields, and send the info to the server
	 * to create a new user account if it is allowable.
	 */
	private void createAccount() {
		String email = mEmail.getText().toString();
		String password = mPassword.getText().toString();
		String confirmPassword = mPasswordConfirm.getText().toString();
		
		if(!validateEmailAddress(email)) {		
			showDialog(DIALOG_INVALID_EMAIL_ID);
			return;
		}
		
		if(!validatePassword(password, confirmPassword)) {
			showDialog(DIALOG_INVALID_PASSWORD_ID);
			return;
		}
		
		mServerCommunicator.sendAccountCreationRequest(email, password);
	}
	
	/**
	 * Checks whether the email field contains a valid Email address
	 * @return a boolean that is true if and only if the Email field contains
	 * 			a valid email address
	 */
	private boolean validateEmailAddress(String email) {
		// This is (obviously) not strong validation, but we aren't too focused on it at the moment
		return  email.matches(".+@.+");
	}
	
	/**
	 * Checks that both the Password and Confirm Password are the same and meet any
	 * constraints on the password
	 * @return a boolean that is true if and only if the text entered into the Password
	 * 			and Confirm Password fields match
	 */
	private boolean validatePassword(String password, String confirmPassword) {
		return confirmPassword.equals(password);
	}
	
	/**
	 * Clear all input fields, forcing the user to start over
	 */
	private void clearInputFields() {
		mEmail.setText("");
		mPassword.setText("");
		mPasswordConfirm.setText("");
	}
	
	/**
	 * Custom handler for the account creation button
	 */
	private class CreateAccountHandler implements View.OnClickListener {
		/**
		 * Log that the button was pressed, and attempt to create a new account
		 */
		public void onClick(View v) {
			Log.d(TAG, "Create Account Button clicked");
            createAccount();
		}
	};
	
	/**
	 * Custom handler to close the error dialog
	 */
	private class CloseDialogListener implements DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
		}
	}

	/**
	 * Handle an error returned from the server
	 */
	public void handleError(String message) {
		// TODO Auto-generated method stub
		
	};
}
