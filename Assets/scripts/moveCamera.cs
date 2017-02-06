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
	Transform target;
	public float moveTime = 0.1f;           //Time it will take object to move, in seconds.
	public LayerMask blockingLayer;         //Layer on which collision will be checked.
	private Quaternion startingRotation;
	public float speed = 10;
	public TextMesh orientationtextref;

	public int object_x_orientation, object_x_orientation_start;
	public int object_y_orientation, object_y_orientation_start;
	public int object_z_orientation, object_z_orientation_start;

	//private BoxCollider2D boxCollider;      //The BoxCollider2D component attached to this object.
	//private Rigidbody2D rb2D;               //The Rigidbody2D component attached to this object.
	private float inverseMoveTime;          //Used to make movement more efficient.

	private DependencyStatus dependencyStatus = DependencyStatus.UnavailableOther;
	// Use this for initialization
	void Start () {
		
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

		object_x_orientation_start = int.Parse(transform.eulerAngles.x.ToString());
		object_y_orientation_start = int.Parse(transform.eulerAngles.y.ToString());
		object_z_orientation_start = int.Parse(transform.eulerAngles.z.ToString());
		object_x_orientation = object_x_orientation_start;
		object_y_orientation = object_y_orientation_start;
		object_z_orientation = object_z_orientation_start;

		string defaulttext = "orientation\nx:" + object_x_orientation.ToString () + "y:" + object_y_orientation.ToString() + " z:" + object_z_orientation.ToString();
		 
		orientationtextref = GameObject.Find("Orientation").GetComponent<TextMesh>();
		orientationtextref.text = defaulttext;

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
				int xloc = (int)float.Parse(e2.Snapshot.Value.ToString());
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
				int yloc = (int)float.Parse(e2.Snapshot.Value.ToString());
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
				int zloc = (int)float.Parse(e2.Snapshot.Value.ToString());
				//hanMoveCam(xloc, yloc, zloc);
				DebugLog("zaxis");
				hanRotateobject(zloc, "zaxis");
			}
		};
	}

	void update_orientation(){
		object_x_orientation = (int)this.transform.rotation.eulerAngles.x;
		object_y_orientation = (int)this.transform.rotation.eulerAngles.y;
		object_z_orientation = (int)this.transform.rotation.eulerAngles.z;
	}

	void hanRotateobject(int rotationAmount, string axis){
		update_orientation ();
		Quaternion finalRotation;
		finalRotation = Quaternion.Euler (0, 0, 0) * startingRotation;
		if (axis == "xaxis") {
			finalRotation = Quaternion.Euler(rotationAmount, object_y_orientation, object_z_orientation ) * startingRotation;
		}
		if (axis == "yaxis") {
			finalRotation = Quaternion.Euler(object_x_orientation, rotationAmount, object_z_orientation ) * startingRotation;
		} 
		if (axis == "zaxis") {
			finalRotation = Quaternion.Euler(object_x_orientation, object_y_orientation, rotationAmount) * startingRotation;
		}
		StartCoroutine (Rotate(finalRotation));
	}

	IEnumerator Rotate(Quaternion finalRotation){
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
		location["xloc"] = 0;
		location["yloc"] = 0;
		location["zloc"] = 0;
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
		update_orientation();
		string updatetext = "orientation\nx:" + object_x_orientation.ToString () + "y:" + object_y_orientation.ToString() + " z:" + object_z_orientation.ToString();
		orientationtextref.text = updatetext;
	}
}