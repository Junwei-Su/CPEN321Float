CREATE TABLE User(user_id INT, name VARCHAR(30), account VARCHAR(30),
	location VARCHAR(30), campain_id INT);
	
CREATE TABLE Campaign(campaign_id INT, initializer VARCHAR(30),
	description VARCHAR(1000), locations VARCHAR(10000), initial_location VARCHAR(30));
	
CREATE TABLE Actions(action_id INT, action_type VARCHAR(30), associated_camp_id INT, 
	associated_user_id INT, date DATE, location VARCHAR(30), user_id INT);
	
CREATE TABLE camp_follower_map(user_id INT, campaign_id INT, date DATE);

CREATE TABLE camp_owner_map(user_id INT, campaign_id INT, date DATE);
