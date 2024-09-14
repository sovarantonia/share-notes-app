import Sidebar from "./sidebar";
import React from "react";
import {useState} from 'react';
import moment from 'moment';
import DatePicker from "react-datepicker";
import {note} from "./api";

import "react-datepicker/dist/react-datepicker.css";
import {useUser} from "./userContext";
import Dropdown from 'react-dropdown';
import 'react-dropdown/style.css';

const CreateNotePage = ({ onLogout }) => {
    const [error, setError] = useState('');
    const [title, setTitle] = useState('');
    const [text, setText] = useState('');
    const [date, setDate] = useState(new Date());
    const [grade, setGrade] = useState(1);

    const { user } = useUser();

    const options = [
        { label: '1', value: 1 },
        { label: '2', value: 2 },
        { label: '3', value: 3 },
        { label: '4', value: 4 },
        { label: '5', value: 5 },
        { label: '6', value: 6 },
        { label: '7', value: 7 },
        { label: '8', value: 8 },
        { label: '9', value: 9 },
        { label: '10', value: 10 },
    ];


    const handleSubmit = async (e) => {
        e.preventDefault();
        const formattedDate = moment(date).format('DD-MM-yyyy');
        if (!title) {
            setError('Enter a title');
            return;
        }

        try {
            await note(user.userId, title, text, formattedDate, grade);
            alert('Note was created');
            setTitle('');
            setText('');
            setDate(new Date());
            setGrade(1);
        } catch (error) {
            setError('An unexpected error occurred.');
        }
    };

    const handleChange = (option) => {
        setGrade(Number(option.value)); // Convert value to integer
    };

    return (
        <div className="create-note-page">
            <Sidebar onLogout={onLogout} />
            <div className="main-content">
                <h2>Create note</h2>
                <div className="create-form">
                    <form onSubmit={handleSubmit} className="form">
                        {error && <div className="error" aria-live="assertive">{error}</div>}
                        <label htmlFor="title">Title: </label>
                        <input
                            type="text"
                            value={title}
                            onChange={e => setTitle(e.target.value)}
                            placeholder="Title"
                            required
                        />

                        <label htmlFor="text">Content: </label>
                        <input
                            type="text"
                            value={text}
                            onChange={e => setText(e.target.value)}
                            placeholder="Text"
                        />

                        <label htmlFor="date">Date: </label>
                        <DatePicker
                            dateFormat="dd-MM-yyyy"
                            maxDate={new Date()}
                            selected={date}
                            onChange={(date) => setDate(date)}
                        />

                        <label htmlFor="grade">Grade: </label>
                        <Dropdown
                            options={options}
                            value={options.find(opt => opt.value === grade)} // Set selected option based on grade
                            onChange={handleChange}
                            placeholder="Select a grade"
                            required
                        />

                        <button type="submit">Submit</button>
                    </form>
                </div>
            </div>
        </div>
    );
};

export default CreateNotePage;