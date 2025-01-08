import requests

base_url = "http://4.175.122.244/"

result = requests.post(f"{base_url}route/create", json={"route_name": "Linha 11", "route_number": 11})
route_id = result.json()["route_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Estação de Aveiro", "stop_location_lat": 40.643771, "stop_location_long": -8.640994})
stop_id11_1 = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Av. Dr. Lourenço Peixinho - CTT B", "stop_location_lat": 40.643384, "stop_location_long": -8.645103})
stop_id11_2_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Av. Dr. Lourenço Peixinho - CTT A", "stop_location_lat": 40.643258, "stop_location_long": -8.644679})
stop_id11_2_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Av. Dr. Lourenço Peixinho - Oita B", "stop_location_lat": 40.642904, "stop_location_long": -8.647506})
stop_id11_3_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Av. Dr. Lourenço Peixinho - Oita A", "stop_location_lat": 40.642761, "stop_location_long": -8.647195})
stop_id11_3_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Av. Dr. Lourenço Peixinho - Capitania B", "stop_location_lat": 40.642077, "stop_location_long": -8.651342})
stop_id11_4_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Av. Dr. Lourenço Peixinho - Capitania A", "stop_location_lat": 40.642004, "stop_location_long": -8.651315})
stop_id11_4_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Caçadores 10 - Misericórdia", "stop_location_lat": 40.640787, "stop_location_long": -8.653032})
stop_id11_5 = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Av. Santa Joana B", "stop_location_lat": 40.638377, "stop_location_long": -8.651841})
stop_id11_6_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Av. Santa Joana A", "stop_location_lat": 40.637799, "stop_location_long": -8.652152})
stop_id11_6_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Universidade - Antiga Reitoria A", "stop_location_lat": 40.634962, "stop_location_long": -8.656958})
stop_id11_7_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Universidade - Antiga Reitoria B", "stop_location_lat": 40.634892, "stop_location_long": -8.65684})
stop_id11_7_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Universidade - Reitoria A", "stop_location_lat": 40.632006, "stop_location_long": -8.657956})
stop_id11_8_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Universidade - Reitoria B", "stop_location_lat": 40.632157, "stop_location_long": -8.658047})
stop_id11_8_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Universidade - Pavilhão A", "stop_location_lat": 40.630137, "stop_location_long": -8.655054})
stop_id11_9_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Universidade - Pavilhão B", "stop_location_lat": 40.629865, "stop_location_long": -8.65477})
stop_id11_9_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Universidade - Santiago A", "stop_location_lat": 40.6298108, "stop_location_long": -8.6595775})
stop_id11_10_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Universidade - Santiago B", "stop_location_lat": 40.629946, "stop_location_long": -8.659104})
stop_id11_10_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Universidade Crasto", "stop_location_lat": 40.623036, "stop_location_long": -8.659233})
stop_id11_11 = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Av. 5 de Outubro", "stop_location_lat": 40.641837, "stop_location_long": -8.646785})
stop_id11_16 = result.json()["stop_id"]

result = requests.post(f"{base_url}bus/create", json={"bus_name": "Bus 11", "route_id": route_id})
bus_id = result.json()["bus_id"]
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_1, "stop_time": "1970-01-01T07:55:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_2_b, "stop_time": "1970-01-01T07:56:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_3_b, "stop_time": "1970-01-01T07:58:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_4_b, "stop_time": "1970-01-01T08:00:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_5, "stop_time": "1970-01-01T08:01:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_6_b, "stop_time": "1970-01-01T08:03:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_7_a, "stop_time": "1970-01-01T08:05:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_8_a, "stop_time": "1970-01-01T08:06:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_9_a, "stop_time": "1970-01-01T08:07:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_10_a, "stop_time": "1970-01-01T08:09:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_11, "stop_time": "1970-01-01T08:11:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_10_b, "stop_time": "1970-01-01T08:14:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_9_b, "stop_time": "1970-01-01T08:16:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_8_b, "stop_time": "1970-01-01T08:17:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_7_b, "stop_time": "1970-01-01T08:18:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_16, "stop_time": "1970-01-01T08:21:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_1, "stop_time": "1970-01-01T08:23:00"})


result = requests.post(f"{base_url}bus/create", json={"bus_name": "Bus 11", "route_id": route_id})
bus_id = result.json()["bus_id"]
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_1, "stop_time": "1970-01-01T08:20:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_2_b, "stop_time": "1970-01-01T08:21:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_3_b, "stop_time": "1970-01-01T08:23:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_4_b, "stop_time": "1970-01-01T08:25:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_5, "stop_time": "1970-01-01T08:26:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_6_b, "stop_time": "1970-01-01T08:28:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_7_a, "stop_time": "1970-01-01T08:30:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_8_a, "stop_time": "1970-01-01T08:31:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_9_a, "stop_time": "1970-01-01T08:32:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_10_a, "stop_time": "1970-01-01T08:34:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_11, "stop_time": "1970-01-01T08:36:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_10_b, "stop_time": "1970-01-01T08:39:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_9_b, "stop_time": "1970-01-01T08:41:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_8_b, "stop_time": "1970-01-01T08:42:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_9_b, "stop_time": "1970-01-01T08:43:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_16, "stop_time": "1970-01-01T08:46:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_1, "stop_time": "1970-01-01T08:48:00"})


result = requests.post(f"{base_url}bus/create", json={"bus_name": "Bus 11", "route_id": route_id})
bus_id = result.json()["bus_id"]
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_1, "stop_time": "1970-01-01T08:50:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_2_b, "stop_time": "1970-01-01T08:51:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_3_b, "stop_time": "1970-01-01T08:53:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_4_b, "stop_time": "1970-01-01T08:55:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_5, "stop_time": "1970-01-01T08:56:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_6_b, "stop_time": "1970-01-01T08:58:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_7_a, "stop_time": "1970-01-01T09:00:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_8_a, "stop_time": "1970-01-01T09:01:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_9_a, "stop_time": "1970-01-01T09:02:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_10_a, "stop_time": "1970-01-01T09:04:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_11, "stop_time": "1970-01-01T09:06:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_10_b, "stop_time": "1970-01-01T09:09:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_9_b, "stop_time": "1970-01-01T09:11:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_8_b, "stop_time": "1970-01-01T09:12:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_9_b, "stop_time": "1970-01-01T09:13:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_16, "stop_time": "1970-01-01T09:16:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_1, "stop_time": "1970-01-01T09:18:00"})


result = requests.post(f"{base_url}route/create", json={"route_name": "Linha 5", "route_number": 5})
route_id = result.json()["route_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Colégio D. José I", "stop_location_lat": 40.631644, "stop_location_long": -8.606549})
stop_id5_1 = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "R. 8 de Dezembro", "stop_location_lat": 40.632202, "stop_location_long": -8.611068})
stop_id5_2 = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "R. do Barreiro A", "stop_location_lat": 40.629822, "stop_location_long": -8.613726})
stop_id5_3_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "R. do Barreiro B", "stop_location_lat": 40.62993, "stop_location_long": -8.61381})
stop_id5_3_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "R. Solposto A", "stop_location_lat": 40.629918, "stop_location_long": -8.615121})
stop_id5_4_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "R. Solposto B", "stop_location_lat": 40.629738, "stop_location_long":  -8.615175})
stop_id5_4_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "R. Solposto / Igreja A", "stop_location_lat": 40.6324316, "stop_location_long": -8.6168472})
stop_id5_5_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "R. Solposto / Igreja B", "stop_location_lat": 40.632458, "stop_location_long": -8.616975})
stop_id5_5_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "R. do Viso 2 A", "stop_location_lat": 40.6373156, "stop_location_long": -8.6202027})
stop_id5_6_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "R. do Viso 2 B", "stop_location_lat": 40.636952, "stop_location_long": -8.619987})
stop_id5_6_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Caião A", "stop_location_lat": 40.639558, "stop_location_long": -8.622084})
stop_id5_7_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Caião B", "stop_location_lat": 40.63992, "stop_location_long": -8.622476})
stop_id5_7_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "R. do Viso 1 A", "stop_location_lat": 40.642598, "stop_location_long": -8.623232})
stop_id5_8_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "R. do Viso 1 B", "stop_location_lat": 40.642651, "stop_location_long": -8.623407})
stop_id5_8_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Gen. Costa Cascais A", "stop_location_lat": 40.644454, "stop_location_long": -8.624686})
stop_id5_9_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Gen. Costa Cascais B", "stop_location_lat": 40.644121, "stop_location_long": -8.624632})
stop_id5_9_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Igreja de Esgueira A", "stop_location_lat": 40.647947, "stop_location_long": -8.626877})
stop_id5_10_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Igreja de Esgueira B", "stop_location_lat": 40.64769, "stop_location_long": -8.62681})
stop_id5_10_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Fonte de Esgueira", "stop_location_lat": 40.649906, "stop_location_long": -8.6271})
stop_id5_11 = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Esgueira - Carramona", "stop_location_lat": 40.6484377, "stop_location_long": -8.6303897})
stop_id5_12 = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Esgueira - Luciano de Castro B", "stop_location_lat": 40.6475284, "stop_location_long": -8.6328842})
stop_id5_13_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Esgueira - Luciano de Castro A", "stop_location_lat": 40.647377, "stop_location_long": -8.633135})
stop_id5_13_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Escola Jaime Magalhães A", "stop_location_lat": 40.646078, "stop_location_long": -8.632003})
stop_id5_14_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Escola Jaime Magalhães B", "stop_location_lat": 40.6460866, "stop_location_long": -8.632443})
stop_id5_14_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Viaduto de Esgueira A", "stop_location_lat": 40.646628, "stop_location_long": -8.634245})
stop_id5_15_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Viaduto de Esgueira B", "stop_location_lat": 40.646518, "stop_location_long": -8.63417})
stop_id5_15_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "R. Luis G. Carvalho", "stop_location_lat": 40.644544, "stop_location_long": -8.642979})
stop_id5_16 = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Hospital / Universidade A", "stop_location_lat": 40.634734, "stop_location_long": -8.656197})
stop_id5_17_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Hospital / Universidade B", "stop_location_lat": 40.634924, "stop_location_long": -8.6557312})
stop_id5_17_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Av. da Universidade A", "stop_location_lat": 40.63152, "stop_location_long": -8.65485})
stop_id5_18_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Av. da Universidade B", "stop_location_lat": 40.631627, "stop_location_long": -8.6547167})
stop_id5_18_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "ISCAUA A", "stop_location_lat": 40.6307003, "stop_location_long": -8.6533816})
stop_id5_19_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "ISCAUA B", "stop_location_lat": 40.6308693, "stop_location_long": -8.6532737})
stop_id5_19_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "R. Nova de Santiago A", "stop_location_lat": 40.6292454, "stop_location_long": -8.6518843})
stop_id5_20_a = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "R. Nova de Santiago B", "stop_location_lat": 40.6292581, "stop_location_long": -8.6516905})
stop_id5_20_b = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Escolas de Santiago", "stop_location_lat": 40.627033, "stop_location_long": -8.649217})
stop_id5_21 = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "R.de Ovar - Urbanização Santiago", "stop_location_lat": 40.627218, "stop_location_long": -8.648201})
stop_id5_22 = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Mercado de Santiago", "stop_location_lat": 40.6270036, "stop_location_long": -8.6508603})
stop_id5_23 = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Caçadores 10 - Sé", "stop_location_lat": 40.639985, "stop_location_long": -8.651159})
stop_id5_29 = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Fórum", "stop_location_lat": 40.640445, "stop_location_long": -8.6518494})
stop_id5_30 = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "R. de Viseu", "stop_location_lat": 40.645256, "stop_location_long": -8.642372})
stop_id5_31 = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "Esgueira - Pelourinho", "stop_location_lat": 40.648244, "stop_location_long": -8.628369})
stop_id5_35 = result.json()["stop_id"]

result = requests.post(f"{base_url}stop/create", json={"stop_name": "R. Molareira", "stop_location_lat": 40.631689, "stop_location_long": -8.610454})
stop_id5_44 = result.json()["stop_id"]

result = requests.post(f"{base_url}bus/create", json={"bus_name": "Bus 5 (Santiago)", "route_id": route_id})
bus_id = result.json()["bus_id"]
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_1, "stop_time": "1970-01-01T06:55:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_2, "stop_time": "1970-01-01T06:56:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_3_a, "stop_time": "1970-01-01T06:56:30"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_4_a, "stop_time": "1970-01-01T06:57:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_5_a, "stop_time": "1970-01-01T06:58:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_6_a, "stop_time": "1970-01-01T06:59:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_7_a, "stop_time": "1970-01-01T07:00:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_8_a, "stop_time": "1970-01-01T07:01:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_9_a, "stop_time": "1970-01-01T07:02:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_10_a, "stop_time": "1970-01-01T07:04:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_11, "stop_time": "1970-01-01T07:05:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_12, "stop_time": "1970-01-01T07:06:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_13_b, "stop_time": "1970-01-01T07:07:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_14_a, "stop_time": "1970-01-01T07:08:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_15_a, "stop_time": "1970-01-01T07:09:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_16, "stop_time": "1970-01-01T07:10:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_2_b, "stop_time": "1970-01-01T07:11:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_3_b, "stop_time": "1970-01-01T07:12:30"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_4_b, "stop_time": "1970-01-01T07:14:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_5, "stop_time": "1970-01-01T07:16:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_6_b, "stop_time": "1970-01-01T07:18:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_17_a, "stop_time": "1970-01-01T07:20:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_18_a, "stop_time": "1970-01-01T07:21:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_19_a, "stop_time": "1970-01-01T07:22:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_20_a, "stop_time": "1970-01-01T07:23:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_21, "stop_time": "1970-01-01T07:24:00"})

result = requests.post(f"{base_url}bus/create", json={"bus_name": "Bus 5 (Solposto)", "route_id": route_id})
bus_id = result.json()["bus_id"]
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_22, "stop_time": "1970-01-01T07:25:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_23, "stop_time": "1970-01-01T07:26:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_20_b, "stop_time": "1970-01-01T07:27:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_19_b, "stop_time": "1970-01-01T07:28:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_18_b, "stop_time": "1970-01-01T07:29:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_17_b, "stop_time": "1970-01-01T07:30:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_6_a, "stop_time": "1970-01-01T07:32:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_29, "stop_time": "1970-01-01T07:34:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_30, "stop_time": "1970-01-01T07:35:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_4_a, "stop_time": "1970-01-01T07:36:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_3_a, "stop_time": "1970-01-01T07:37:30"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_2_a, "stop_time": "1970-01-01T07:39:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_1, "stop_time": "1970-01-01T07:40:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_31, "stop_time": "1970-01-01T07:42:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_15_b, "stop_time": "1970-01-01T07:43:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_14_b, "stop_time": "1970-01-01T07:44:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_13_a, "stop_time": "1970-01-01T07:45:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_35, "stop_time": "1970-01-01T07:45:30"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_10_b, "stop_time": "1970-01-01T07:46:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_9_b, "stop_time": "1970-01-01T07:48:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_8_b, "stop_time": "1970-01-01T07:49:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_7_b, "stop_time": "1970-01-01T07:50:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_6_b, "stop_time": "1970-01-01T07:51:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_5_b, "stop_time": "1970-01-01T07:52:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_4_b, "stop_time": "1970-01-01T07:53:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_3_b, "stop_time": "1970-01-01T07:53:30"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_44, "stop_time": "1970-01-01T07:54:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_1, "stop_time": "1970-01-01T07:55:00"})

result = requests.post(f"{base_url}bus/create", json={"bus_name": "Bus 5 (Santiago)", "route_id": route_id})
bus_id = result.json()["bus_id"]
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_1, "stop_time": "1970-01-01T07:45:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_2, "stop_time": "1970-01-01T07:46:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_3_a, "stop_time": "1970-01-01T07:46:30"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_4_a, "stop_time": "1970-01-01T07:47:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_5_a, "stop_time": "1970-01-01T07:48:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_6_a, "stop_time": "1970-01-01T07:49:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_7_a, "stop_time": "1970-01-01T07:50:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_8_a, "stop_time": "1970-01-01T07:51:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_9_a, "stop_time": "1970-01-01T07:52:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_10_a, "stop_time": "1970-01-01T07:54:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_11, "stop_time": "1970-01-01T07:55:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_12, "stop_time": "1970-01-01T07:56:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_13_b, "stop_time": "1970-01-01T07:57:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_14_a, "stop_time": "1970-01-01T07:58:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_15_a, "stop_time": "1970-01-01T07:59:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_16, "stop_time": "1970-01-01T08:00:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_2_b, "stop_time": "1970-01-01T08:01:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_3_b, "stop_time": "1970-01-01T08:02:30"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_4_b, "stop_time": "1970-01-01T08:04:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_5, "stop_time": "1970-01-01T08:06:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_6_b, "stop_time": "1970-01-01T08:08:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_17_a, "stop_time": "1970-01-01T08:10:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_18_a, "stop_time": "1970-01-01T08:11:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_19_a, "stop_time": "1970-01-01T08:12:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_20_a, "stop_time": "1970-01-01T08:13:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_21, "stop_time": "1970-01-01T08:14:00"})

result = requests.post(f"{base_url}bus/create", json={"bus_name": "Bus 5 (Solposto)", "route_id": route_id})
bus_id = result.json()["bus_id"]
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_22, "stop_time": "1970-01-01T07:55:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_23, "stop_time": "1970-01-01T07:56:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_20_b, "stop_time": "1970-01-01T07:57:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_19_b, "stop_time": "1970-01-01T07:58:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_18_b, "stop_time": "1970-01-01T07:59:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_17_b, "stop_time": "1970-01-01T08:00:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_6_a, "stop_time": "1970-01-01T08:02:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_29, "stop_time": "1970-01-01T08:04:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_30, "stop_time": "1970-01-01T08:05:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_4_a, "stop_time": "1970-01-01T08:06:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_3_a, "stop_time": "1970-01-01T08:07:30"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_2_a, "stop_time": "1970-01-01T08:09:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_1, "stop_time": "1970-01-01T08:10:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_31, "stop_time": "1970-01-01T08:12:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_15_b, "stop_time": "1970-01-01T08:13:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_14_b, "stop_time": "1970-01-01T08:14:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_13_a, "stop_time": "1970-01-01T08:15:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_35, "stop_time": "1970-01-01T08:15:30"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_10_b, "stop_time": "1970-01-01T08:16:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_9_b, "stop_time": "1970-01-01T08:18:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_8_b, "stop_time": "1970-01-01T08:19:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_7_b, "stop_time": "1970-01-01T08:20:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_6_b, "stop_time": "1970-01-01T08:21:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_5_b, "stop_time": "1970-01-01T08:22:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_4_b, "stop_time": "1970-01-01T08:23:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_3_b, "stop_time": "1970-01-01T08:23:30"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_44, "stop_time": "1970-01-01T08:24:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_1, "stop_time": "1970-01-01T08:25:00"})

result = requests.post(f"{base_url}bus/create", json={"bus_name": "Bus 5 (Santiago)", "route_id": route_id})
bus_id = result.json()["bus_id"]
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_1, "stop_time": "1970-01-01T09:00:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_2, "stop_time": "1970-01-01T09:01:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_3_a, "stop_time": "1970-01-01T09:01:30"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_4_a, "stop_time": "1970-01-01T09:02:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_5_a, "stop_time": "1970-01-01T09:03:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_6_a, "stop_time": "1970-01-01T09:04:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_7_a, "stop_time": "1970-01-01T09:05:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_8_a, "stop_time": "1970-01-01T09:06:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_9_a, "stop_time": "1970-01-01T09:07:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_10_a, "stop_time": "1970-01-01T09:09:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_11, "stop_time": "1970-01-01T09:10:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_12, "stop_time": "1970-01-01T09:11:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_13_b, "stop_time": "1970-01-01T09:12:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_14_a, "stop_time": "1970-01-01T09:13:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_15_a, "stop_time": "1970-01-01T09:14:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_16, "stop_time": "1970-01-01T09:15:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_2_b, "stop_time": "1970-01-01T09:16:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_3_b, "stop_time": "1970-01-01T09:17:30"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_4_b, "stop_time": "1970-01-01T09:19:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_5, "stop_time": "1970-01-01T09:21:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_6_b, "stop_time": "1970-01-01T09:23:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_17_a, "stop_time": "1970-01-01T09:25:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_18_a, "stop_time": "1970-01-01T09:26:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_19_a, "stop_time": "1970-01-01T09:27:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_20_a, "stop_time": "1970-01-01T09:28:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_21, "stop_time": "1970-01-01T09:29:00"})

result = requests.post(f"{base_url}bus/create", json={"bus_name": "Bus 5 (Solposto)", "route_id": route_id})
bus_id = result.json()["bus_id"]
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_22, "stop_time": "1970-01-01T08:25:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_23, "stop_time": "1970-01-01T08:26:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_20_b, "stop_time": "1970-01-01T08:27:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_19_b, "stop_time": "1970-01-01T08:28:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_18_b, "stop_time": "1970-01-01T08:29:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_17_b, "stop_time": "1970-01-01T08:30:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_6_a, "stop_time": "1970-01-01T08:32:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_29, "stop_time": "1970-01-01T08:34:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_30, "stop_time": "1970-01-01T08:35:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_4_a, "stop_time": "1970-01-01T08:36:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_3_a, "stop_time": "1970-01-01T08:37:30"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_2_a, "stop_time": "1970-01-01T08:39:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id11_1, "stop_time": "1970-01-01T08:40:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_31, "stop_time": "1970-01-01T08:42:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_15_b, "stop_time": "1970-01-01T08:43:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_14_b, "stop_time": "1970-01-01T08:44:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_13_a, "stop_time": "1970-01-01T08:45:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_35, "stop_time": "1970-01-01T08:45:30"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_10_b, "stop_time": "1970-01-01T08:46:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_9_b, "stop_time": "1970-01-01T08:48:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_8_b, "stop_time": "1970-01-01T08:49:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_7_b, "stop_time": "1970-01-01T08:50:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_6_b, "stop_time": "1970-01-01T08:51:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_5_b, "stop_time": "1970-01-01T08:52:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_4_b, "stop_time": "1970-01-01T08:53:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_3_b, "stop_time": "1970-01-01T08:53:30"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_44, "stop_time": "1970-01-01T08:54:00"})
result = requests.post(f"{base_url}bus/add_stop", json={"bus_id": bus_id, "stop_id": stop_id5_1, "stop_time": "1970-01-01T08:55:00"})