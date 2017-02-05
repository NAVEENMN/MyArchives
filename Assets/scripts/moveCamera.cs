using Firebase;
using Firebase.Database;
using Firebase.Unity.Editor;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System.Threading.Tasks;
using System.Text;
using UnityEngine.UI;

public class moveCamera : MonoBehaviour {

	private string logText = "";
	const int kMaxLogSize = 16382;
	public float cam_start_x, cam_start_y, cam_start_z;
	public static float cam_current_x, cam_current_y, cam_current_z;
	Transform target;
	public float moveTime = 0.1f;           //Time it will take object to move, in seconds.
	public LayerMask blockingLayer;         //Layer on which collision will be checked.
	private Quaternion startingRotation;
	public float speed = 10;


	//private BoxCollider2D boxCollider;      //The BoxCollider2D component attached to this object.
	//private Rigidbody2D rb2D;               //The Rigidbody2D component attached to this object.
	private float inverseMoveTime;          //Used to make movement more efficient.

	private DependencyStatus dependencyStatus = DependencyStatus.UnavailableOther;
	// Use this for initialization
	void Start () {

		try
		{
			Network.TestConnection();
			DebugLog("No network");
		}
		catch (System.Exception)
		{
			//do something if code does not work
			DebugLog("check network.");
		}
	
		startingRotation = this.transform.rotation;

		dependencyStatus = FirebaseApp.CheckDependencies();
		if (dependencyStatus != DependencyStatus.Available) {
			FirebaseApp.FixDependenciesAsync().ContinueWith(task => {
				dependencyStatus = FirebaseApp.CheckDependencies();
				if (dependencyStatus == DependencyStatus.Available) {
					InitializeFirebase();
				} else {
					// This should never happen if we're only using Firebase Analytics.
					// It does not rely on any external dependencies.
					Debug.LogError(
						"Could not resolve all Firebase dependencies: " + dependencyStatus);
				}
			});
		} else {
			InitializeFirebase();
		}

		cam_start_x = float.Parse(transform.eulerAngles.x.ToString ());
		cam_start_y = float.Parse(transform.eulerAngles.y.ToString ());
		cam_start_z = float.Parse(transform.eulerAngles.z.ToString ());

		//Get a component reference to this object's BoxCollider2D
		//boxCollider = GetComponent <BoxCollider2D> ();

		//Get a component reference to this object's Rigidbody2D
		//rb2D = GetComponent <Rigidbody2D> ();

		//By storing the reciprocal of the move time we can use it by multiplying instead of dividing, this is more efficient.
		inverseMoveTime = 1f / moveTime;
	}

	// Output text to the debug log text field, as well as the console.
	public void DebugLog(string s) {
		Debug.Log(s);
		logText += s + "\n";

		while (logText.Length > kMaxLogSize) {
			int index = logText.IndexOf("\n");
			logText = logText.Substring(index + 1);
		}
	}

	// Initialize the Firebase database:
	void InitializeFirebase() {
		FirebaseApp app = FirebaseApp.DefaultInstance;
		app.SetEditorDatabaseUrl("https://mathvr-73393.firebaseio.com/");

		// Initilize data to FB
		DatabaseReference reference = FirebaseDatabase.DefaultInstance.GetReference("RightHand");
		DebugLog("Running Transaction...");
		reference.RunTransaction(InitlocationonFB)
			.ContinueWith(task => {
				if (task.Exception != null) {
					DebugLog(task.Exception.ToString());
				} else if (task.IsCompleted) {
					DebugLog("Transaction complete.");
				}
			});
		//set up listeners
		FirebaseDatabase.DefaultInstance.GetReference("RightHand").Child("xloc")
			.ValueChanged += (object sender2, ValueChangedEventArgs e2) => {
			if (e2.DatabaseError != null) {
				Debug.LogError(e2.DatabaseError.Message);
				return;
			}
			if (e2.Snapshot != null) {
				float xloc = float.Parse(e2.Snapshot.Value.ToString());
				//float yloc = float.Parse(e2.Snapshot.Child("yloc").Value.ToString());
				//float zloc = float.Parse(e2.Snapshot.Child("zloc").Value.ToString());
				//hanMoveCam(xloc, yloc, zloc);
				DebugLog("xaxis");
				hanRotateobject(xloc, "xaxis");
			}
		};
		//set up listeners
		FirebaseDatabase.DefaultInstance.GetReference("RightHand").Child("yloc")
			.ValueChanged += (object sender2, ValueChangedEventArgs e2) => {
			if (e2.DatabaseError != null) {
				Debug.LogError(e2.DatabaseError.Message);
				return;
			}
			if (e2.Snapshot != null) {
				//float xloc = float.Parse(e2.Snapshot.Child("xloc").Value.ToString());
				float yloc = float.Parse(e2.Snapshot.Value.ToString());
				//float zloc = float.Parse(e2.Snapshot.Child("zloc").Value.ToString());
				DebugLog("yaxis");
				hanRotateobject(yloc, "yaxis");
			}
		};
		//set up listeners
		FirebaseDatabase.DefaultInstance.GetReference("RightHand").Child("zloc")
			.ValueChanged += (object sender2, ValueChangedEventArgs e2) => {
			if (e2.DatabaseError != null) {
				Debug.LogError(e2.DatabaseError.Message);
				return;
			}
			if (e2.Snapshot != null) {
				float zloc = float.Parse(e2.Snapshot.Value.ToString());
				//hanMoveCam(xloc, yloc, zloc);
				DebugLog("zaxis");
				hanRotateobject(zloc, "zaxis");
			}
		};
	}

	void hanRotateobject(float degree, string axis){
		if (axis == "xaxis") {
			StartCoroutine (Rotatex (degree));
		} else if (axis == "yaxis") {
			StartCoroutine (Rotatey (degree));
		} else {
			StartCoroutine (Rotatez (degree));
		}
	}

	IEnumerator Rotatex(float rotationAmount){
		cam_current_x = float.Parse(this.transform.rotation.x.ToString());
		cam_current_y = float.Parse(this.transform.rotation.y.ToString());
		cam_current_z = float.Parse(this.transform.rotation.z.ToString());

		Quaternion finalRotation = Quaternion.Euler( rotationAmount, this.transform.rotation.y, this.transform.rotation.z ) * startingRotation;

		while(this.transform.rotation != finalRotation){
			this.transform.rotation = Quaternion.Lerp(this.transform.rotation, finalRotation, Time.deltaTime*speed);
			yield return 0;
		}
	}
	IEnumerator Rotatey(float rotationAmount){
		cam_current_x = float.Parse(this.transform.rotation.x.ToString());
		cam_current_y = float.Parse(this.transform.rotation.y.ToString());
		cam_current_z = float.Parse(this.transform.rotation.z.ToString());

		DebugLog(this.transform.rotation.x.ToString());
		DebugLog(this.transform.rotation.y.ToString());
		DebugLog(this.transform.rotation.z.ToString());
		Quaternion finalRotation = Quaternion.Euler( this.transform.rotation.x, rotationAmount, this.transform.rotation.z ) * startingRotation;

		while(this.transform.rotation != finalRotation){
			this.transform.rotation = Quaternion.Lerp(this.transform.rotation, finalRotation, Time.deltaTime*speed);
			yield return 0;
		}
	}
	IEnumerator Rotatez(float rotationAmount){
		cam_current_x = float.Parse(this.transform.rotation.x.ToString());
		cam_current_y = float.Parse(this.transform.rotation.y.ToString());
		cam_current_z = float.Parse(this.transform.rotation.z.ToString());

		Quaternion finalRotation = Quaternion.Euler( this.transform.rotation.x, this.transform.rotation.y, rotationAmount ) * startingRotation;

		while(this.transform.rotation != finalRotation){
			this.transform.rotation = Quaternion.Lerp(this.transform.rotation, finalRotation, Time.deltaTime*speed);
			yield return 0;
		}
	}

	void hanMoveCam(float xloc, float yloc, float zloc){
		//transform.Translate (-0.05f, 0f, 0f);
		//MoveCam (xloc, yloc);
		Vector3 pointA = transform.position;
		Vector3 pointB = new Vector3 (xloc, yloc, zloc);
		DebugLog(pointB.x.ToString());
		if ((pointA.x != pointB.x) || (pointA.y != pointB.y)) {
			StartCoroutine (MoveObject (transform, pointA, pointB, 3.0f));
		}
		//StartCoroutine(MoveObject(transform, pointB, pointA, 3.0f));
	}

	IEnumerator MoveObject (Transform thisTransform, Vector3 startPos, Vector3 endPos, float time) {
		float i = 0.0f;
		float rate = 1.0f / time;
		DebugLog("comes2 here");
		while (i < 1.0f) {
			i += Time.deltaTime * rate;
			transform.Translate (0.05f, 0f, 0f);
			DebugLog ("moving from");
			DebugLog (transform.position.x.ToString());
			DebugLog (transform.position.y.ToString());
			DebugLog ("to");
			DebugLog (endPos.x.ToString());
			DebugLog (endPos.y.ToString());
			thisTransform.position = Vector3.Lerp(startPos, endPos, i);
			yield return null; 
		}
	}


	//Move returns true if it is able to move and false if not. 
	//Move takes parameters for x direction, y direction and a RaycastHit2D to check collision.
	void MoveCam (float xDir, float yDir)
	{
		//Store start position to move from, based on objects current transform position.
		Vector2 start = transform.position;

		// Calculate end position based on the direction parameters passed in when calling Move.
		Vector2 end = start + new Vector2 (xDir, yDir);

		StartCoroutine (SmoothMovement (start, end));
	}

	protected IEnumerator SmoothMovement (Vector3 start, Vector3 end)
	{
		//Calculate the remaining distance to move based on the square magnitude of the difference between current position and end parameter. 
		//Square magnitude is used instead of magnitude because it's computationally cheaper.
		float sqrRemainingDistance = (transform.position - end).sqrMagnitude;

		//While that distance is greater than a very small amount (Epsilon, almost zero):
		while(sqrRemainingDistance > float.Epsilon)
		{
			//Find a new position proportionally closer to the end, based on the moveTime
			start = Vector3.MoveTowards(start, end, inverseMoveTime * Time.deltaTime);

			//Call MovePosition on attached Rigidbody2D and move it to the calculated position.
			//rb2D.MovePosition (newPostion);

			//Recalculate the remaining distance after moving.
			sqrRemainingDistance = (transform.position - end).sqrMagnitude;

			//Return and loop until sqrRemainingDistance is close enough to zero to end the function
			yield return null;
		}
	}


	TransactionResult InitlocationonFB(MutableData mutableData) {
		// Now we add the new score as a new entry that contains the email address and score.
		Dictionary<string, object> location = new Dictionary<string, object>();
		location["xloc"] = 0.0f;
		location["yloc"] = 0.0f;
		location["zloc"] = 0.0f;
		// You must set the Value to indicate data at that location has changed.
		mutableData.Value = location;
		return TransactionResult.Success(mutableData);
	}

	// Update is called once per frame
	void Update () {
		if(Input.GetKey(KeyCode.W)){
			transform.Translate (0.05f, 0f, 0f);
			DebugLog (transform.position.x.ToString());
		}
		if(Input.GetKey(KeyCode.S)){
			transform.Translate (-0.05f, 0f, 0f);
		}
		if(Input.GetKey(KeyCode.A)){
			transform.Translate (0.0f, 0f, 0.05f);
		}
		if(Input.GetKey(KeyCode.D)){
			transform.Translate (0.0f, 0f, -0.05f);
		}
	}
}