# Pre-work - *DaList*

**DaList** is an android app that allows building a todo list and basic todo items management functionality including adding new items, editing and deleting an existing item.

Submitted by: **Simon Adediran**

Time spent: **7** hours spent in total

## User Stories

The following **required** functionality is completed:

* [ ]  User can **successfully add and remove items** from the todo list
* [ ]  User can **tap a todo item in the list and bring up an edit screen for the todo item** and then have any changes to the text reflected in  the todo list.
* [ ]  User can persist todo items and retrieve them properly on app restart

The following **optional** features are implemented:

* [ ]   Persist the todo items [into SQLite] instead of a text file
* [ ]  	Improve style of the todo items in the list [using a custom adapter]
* [ ]   Add support for completion due dates for todo items (and display within listview item)
* [ ]   Use a [DialogFragment] to inform users of unsaved changes while editing
* [ ]   Add support for selecting the priority(HIGH, MEDIUM, LOW) of each todo item (and display in listview item). 
* [ ]  	Tweak the style improving the UI / UX, play with colors, images or backgrounds

The following **additional** features are implemented:

* [ ]  Use a [DialogFragment] to inform users of unsaved changes while editing

## Video Walkthrough

Here's a walkthrough of implemented user stories:

<img src='demo.gif' title='Video Walkthrough' width='' alt='Video Walkthrough' />

GIF created with [LiceCap](http://www.cockos.com/licecap/).

## Project Analysis

As part of your pre-work submission, please reflect on the app and answer the following questions below:

**Question 1:** "What are your reactions to the Android app development platform so far? Compare and contrast Android's approach to layouts and user interfaces in past platforms you've used."

**Answer:** The platform makes implementing user interfaces easy and friendly with lot of customization

**Question 2:** "Take a moment to reflect on the `ArrayAdapter` used in your pre-work. How would you describe an adapter in this context and what is its function in Android? Why do you think the adapter is important? Explain the purpose of the `convertView` in the `getView` method of the `ArrayAdapter`."

ArrayAdapter used by android helps to attach or present data from any data source to a UI element. Or can be said to return a view per data/object in a data source

**Answer:** ConvertView track views that can be reuse, this helps to improve the performance of the app by ensuring better resource utilization.


## Notes

No majot challenges were faced.

## License

    Copyright @2017 Simon Adediran
	
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
