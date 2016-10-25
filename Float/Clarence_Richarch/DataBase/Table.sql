CREAT TABLE User(user_id INT, name VARCHAR(30), account VARCHAR(30),
	ParticipateCampList VARCHAR(10000), InitializedCampList VARCHAR(10000),
	location VARCHAR(30), action_list VARCHAR(10000), campain_id INT);
	
	
CREAT TABLE Campaign(campaign_id INT, initializer VARCHAR(30), participator VARCHAR(30),
	description VARCHAR(1000), locations VARCHAR(10000), initial_location VARCHAR(30));
	
CREAT TABLE Actions(action_id INT, action_type VARCHAR(30), associated_camp_id INT, 
	associated_user_id INT, date DATE, location VARCHAR(30), user_id INT);
