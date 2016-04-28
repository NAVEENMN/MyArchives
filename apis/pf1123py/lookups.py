
db_type = ["mongo", "firebase"]
table_ids = ["ADB", "EVNT", "MOV", "THR", "ST"]


add_params_list = ["mid", "name", "email", "fb_id", "dev_id", "invites", "hosted", "joined","food_pref","movie_pref", "latitude", "longitude", "what", "gender"]
mov_prams_list = ["url","movie_name","movie_year", "mid","mov_id", "mov_name", "release_date", "language", "genre", "year"]
evnt_prams_list = ["email", "mid","event_name", "event_date", "event_time", "event_notes", "event_host", "event_members","host_email", "event_id"]
thr_prams_list = ["latitude", "longitude"]
st_prams_list = ["theater_mid", "movie_mid", "show_time"]

params = dict()
params["add_params"] = add_params_list
params["mov_params"] = mov_prams_list
params["evnt_params"] = evnt_prams_list
params["thr_params"] = thr_prams_list
params["st_params"] = st_prams_list
