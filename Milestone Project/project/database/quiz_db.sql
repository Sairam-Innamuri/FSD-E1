-- Automated Quiz Engine Database Schema
-- Run this in phpMyAdmin or MySQL to create the database

CREATE DATABASE IF NOT EXISTS quiz_engine;
USE quiz_engine;

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Quiz attempts / results
CREATE TABLE IF NOT EXISTS attempts (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    score INT NOT NULL,
    total_questions INT DEFAULT 10,
    badge VARCHAR(20) NOT NULL,
    quiz_category VARCHAR(50) NOT NULL DEFAULT 'general_knowledge',
    time_taken INT DEFAULT 0,
    completed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

-- Questions table with categories
CREATE TABLE IF NOT EXISTS questions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    category VARCHAR(50) NOT NULL,
    question_text TEXT NOT NULL,
    option_a VARCHAR(255) NOT NULL,
    option_b VARCHAR(255) NOT NULL,
    option_c VARCHAR(255) NOT NULL,
    option_d VARCHAR(255) NOT NULL,
    correct_answer CHAR(1) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Insert questions for multiple quiz categories
-- 1) Movie quiz (actor and movie)
INSERT INTO questions (category, question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES
('movies', 'In which movie did Shah Rukh Khan play the lead role of a coach?', 'Dangal', 'Chak De! India', '3 Idiots', 'Lagaan', 'B'),
('movies', 'Which movie stars Aamir Khan as an inspiring teacher of troubled students?', 'PK', 'Taare Zameen Par', 'Ghajini', 'Dhoom 3', 'B'),
('movies', 'In which movie did Rajinikanth act as a scientist who creates a robot?', 'Robot (Enthiran)', 'Kabali', 'Sivaji', 'Darbar', 'A'),
('movies', 'Which movie features Deepika Padukone as an athlete?', 'Mary Kom', 'Chak De! India', 'Piku', '83', 'D'),
('movies', 'In which movie did Salman Khan act as a man who helps a lost girl reach Pakistan?', 'Sultan', 'Bajrangi Bhaijaan', 'Kick', 'Tiger Zinda Hai', 'B'),
('movies', 'Which movie has Hrithik Roshan playing a brilliant mathematician''s student and later a teacher?', 'Super 30', 'Krrish', 'Bang Bang', 'Kabhi Khushi Kabhie Gham', 'A'),
('movies', 'In which movie did Akshay Kumar play the role of a hockey player turned coach?', 'Gold', 'Kesari', 'Airlift', 'Holiday', 'A'),
('movies', 'Which movie stars Alia Bhatt as a spy married to a Pakistani officer?', 'Highway', 'Raazi', 'Student of the Year', 'Gangubai Kathiawadi', 'B'),
('movies', 'In which movie did Ranveer Singh portray the Indian cricket captain Kapil Dev?', 'Padmaavat', 'Simmba', '83', 'Gully Boy', 'C'),
('movies', 'Which movie features Priyanka Chopra in the role of a boxer inspired by a real Indian athlete?', 'Mary Kom', 'Fashion', 'Bajirao Mastani', 'The Sky Is Pink', 'A');

-- 2) Indian capitals
INSERT INTO questions (category, question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES
('indian_capitals', 'What is the capital of India?', 'New Delhi', 'Mumbai', 'Kolkata', 'Chennai', 'A'),
('indian_capitals', 'What is the capital of Tamil Nadu?', 'Hyderabad', 'Chennai', 'Bengaluru', 'Kochi', 'B'),
('indian_capitals', 'What is the capital of Maharashtra?', 'Nagpur', 'Mumbai', 'Pune', 'Nashik', 'B'),
('indian_capitals', 'What is the capital of Karnataka?', 'Mysuru', 'Mangaluru', 'Bengaluru', 'Hubballi', 'C'),
('indian_capitals', 'What is the capital of Gujarat?', 'Ahmedabad', 'Gandhinagar', 'Surat', 'Rajkot', 'B'),
('indian_capitals', 'What is the capital of West Bengal?', 'Kolkata', 'Siliguri', 'Durgapur', 'Howrah', 'A'),
('indian_capitals', 'What is the capital of Rajasthan?', 'Jaipur', 'Udaipur', 'Jodhpur', 'Kota', 'A'),
('indian_capitals', 'What is the capital of Telangana?', 'Vijayawada', 'Warangal', 'Hyderabad', 'Nizamabad', 'C'),
('indian_capitals', 'What is the capital of Bihar?', 'Gaya', 'Patna', 'Bhagalpur', 'Muzaffarpur', 'B'),
('indian_capitals', 'What is the capital of Kerala?', 'Kochi', 'Thiruvananthapuram', 'Kozhikode', 'Thrissur', 'B');

-- 3) Famous places in India
INSERT INTO questions (category, question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES
('famous_places', 'Where is the Taj Mahal located?', 'Jaipur', 'Agra', 'Delhi', 'Lucknow', 'B'),
('famous_places', 'In which city is the Gateway of India located?', 'Mumbai', 'Kolkata', 'Chennai', 'Pune', 'A'),
('famous_places', 'In which state is the Golden Temple (Harmandir Sahib) located?', 'Punjab', 'Haryana', 'Uttar Pradesh', 'Rajasthan', 'A'),
('famous_places', 'Where is the Charminar situated?', 'Hyderabad', 'Bengaluru', 'Chennai', 'Pune', 'A'),
('famous_places', 'In which city is India Gate located?', 'New Delhi', 'Mumbai', 'Chandigarh', 'Bengaluru', 'A'),
('famous_places', 'Where are the Ajanta and Ellora caves located?', 'Gujarat', 'Maharashtra', 'Madhya Pradesh', 'Rajasthan', 'B'),
('famous_places', 'In which state is the backwater destination Alleppey found?', 'Kerala', 'Goa', 'Tamil Nadu', 'Karnataka', 'A'),
('famous_places', 'Where is the Hawa Mahal located?', 'Udaipur', 'Jaipur', 'Jodhpur', 'Jaisalmer', 'B'),
('famous_places', 'In which state is the Konark Sun Temple located?', 'Odisha', 'Andhra Pradesh', 'West Bengal', 'Bihar', 'A'),
('famous_places', 'Where is the hill station Shimla located?', 'Uttarakhand', 'Himachal Pradesh', 'Sikkim', 'Jammu and Kashmir', 'B');

-- 4) General knowledge (including leaders)
INSERT INTO questions (category, question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES
('general_knowledge', 'Who is known as the Father of the Nation in India?', 'Bhagat Singh', 'Mahatma Gandhi', 'Subhas Chandra Bose', 'Jawaharlal Nehru', 'B'),
('general_knowledge', 'Who is the first Prime Minister of India?', 'Jawaharlal Nehru', 'Lal Bahadur Shastri', 'Indira Gandhi', 'Dr. B.R. Ambedkar', 'A'),
('general_knowledge', 'How many players are there in a cricket team on the field?', '9', '10', '11', '12', 'C'),
('general_knowledge', 'Which is the largest planet in our solar system?', 'Earth', 'Jupiter', 'Saturn', 'Mars', 'B'),
('general_knowledge', 'What is the national animal of India?', 'Tiger', 'Lion', 'Elephant', 'Peacock', 'A'),
('general_knowledge', 'What is the national flower of India?', 'Rose', 'Lotus', 'Sunflower', 'Jasmine', 'B'),
('general_knowledge', 'Which festival is known as the Festival of Lights?', 'Holi', 'Diwali', 'Eid', 'Pongal', 'B'),
('general_knowledge', 'Which gas do plants absorb from the atmosphere?', 'Oxygen', 'Nitrogen', 'Carbon Dioxide', 'Hydrogen', 'C'),
('general_knowledge', 'Which is the longest river in India?', 'Ganga', 'Yamuna', 'Narmada', 'Godavari', 'A'),
('general_knowledge', 'Which is the smallest state in India by area?', 'Sikkim', 'Goa', 'Tripura', 'Manipur', 'B');

-- 5) Sports and games
INSERT INTO questions (category, question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES
('sports', 'Who is known as the "God of Cricket" in India?', 'Rahul Dravid', 'Sachin Tendulkar', 'Virat Kohli', 'MS Dhoni', 'B'),
('sports', 'How many rings are there in the Olympic logo?', '3', '4', '5', '6', 'C'),
('sports', 'Which country hosts the Wimbledon tennis tournament?', 'USA', 'Australia', 'France', 'United Kingdom', 'D'),
('sports', 'Which sport is Mary Kom associated with?', 'Badminton', 'Boxing', 'Wrestling', 'Shooting', 'B'),
('sports', 'Which country won the ICC Cricket World Cup 2011?', 'Australia', 'India', 'Sri Lanka', 'Pakistan', 'B'),
('sports', 'In which sport is a shuttlecock used?', 'Tennis', 'Table Tennis', 'Badminton', 'Squash', 'C'),
('sports', 'What is the national sport of India (traditionally recognised)?', 'Hockey', 'Cricket', 'Kabaddi', 'Football', 'A'),
('sports', 'Who is famous as the "Flying Sikh" of India?', 'Milkha Singh', 'P. T. Usha', 'Abhinav Bindra', 'Neeraj Chopra', 'A'),
('sports', 'Which Indian city hosted the 2010 Commonwealth Games?', 'Mumbai', 'New Delhi', 'Chennai', 'Hyderabad', 'B'),
('sports', 'How many players are there on a football team on the field?', '9', '10', '11', '12', 'C');
