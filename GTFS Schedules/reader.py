import re

write_file = open('schedules.txt', 'w', encoding='utf-8')

acronyms = []

with open ('insert.py', 'r', encoding='utf-8') as file:
    data = file.read()

    for line in data.split('\n'):
        if 'stop/create' in line:
            # result = requests.post(f"{base_url}stop/create", json={"stop_name": "Estação de Aveiro", "stop_location_lat": 40.643771, "stop_location_long": -8.640994})
            # find the text after "stop_name": and between the quotes
            stop_name = re.search(r'stop_name": "(.*?)"', line).group(1)
            # find the text after "stop_location_lat": and only numbers
            stop_location_lat = re.search(r'stop_location_lat": (.*?),', line).group(1)
            # find the text after "stop_location_long": and only numbers
            stop_location_long = re.search(r'stop_location_long": (.*?)\}', line).group(1)
            # select all uppercase letters and numbers from the stop_name and join them
            acronym = ''.join(re.findall(r'[A-Z0-9]', stop_name))
            # check if the acronym is already in the list
            if acronym not in acronyms:
                acronyms.append(acronym)
            else:
                print(f'Acronym {acronym} already exists')
            write_file.write(f'{acronym},{stop_name},{stop_location_lat},{stop_location_long}\n')

write_file.close()