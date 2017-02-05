using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class objectaxistext : MonoBehaviour {
	//Text txt;
	// Use this for initialization
	private static moveCamera objectbox;// = GetComponent<moveCamera>();
	void Start () {
		
	}
	
	// Update is called once per frame
	void Update () {
		Debug.Log("text");
		Debug.Log(moveCamera.cam_current_x.ToString());
	}
}
