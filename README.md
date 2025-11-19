📅  Calendar View for Android

<img width="135" height="285" alt="Screenshot_1763532605" src="https://github.com/user-attachments/assets/c26e6323-aab7-4da6-9a7a-04a2d10ac801" />


A customizable iOS-like Calendar View for Android, designed to match the clean and modern style of the native iOS calendar component.

This library provides a fully customizable monthly calendar widget with support for styling header, weekdays, day cells, today indicator, selection state, navigation icons, and more.

✨ Features

iOS-style calendar UI

Customizable header (background, text color, typography, height)

Customizable weekday labels

Customizable day cells (normal, selected, today, other-month days)

Customizable previous/next month navigation icons

Supports picking month/year via an optional dropdown icon

Set any day as the first day of week (Sunday/Monday/etc.)

Control visibility of other-month days

Fully themable via XML and programmatic API

🚀 Installation

Add the library to your build.gradle:
dependencies {
    implementation 'com.yourpackage:ioscalendar:1.0.0'
}

🧩 Usage
1. Add the view in XML
<com.yourpackage.IosCalendarView
    android:id="@+id/iosCalendar"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:headerBackground="@drawable/bg_header"
    app:headerTextColor="@color/white"
    app:weekdayTextColor="@color/gray"
    app:dayTextColor="@color/black"
    app:selectedDayColor="@color/white"
    app:todayTextColor="@color/red"
    app:firstDayOfWeek="sunday"
    app:weekdayLabelMode="one_letter" />

🛠 Programmatic Usage

val calendar = findViewById<IosCalendarView>(R.id.iosCalendar)

calendar.setSelectedDate(Date())
calendar.setFirstDayOfWeek(Calendar.MONDAY)
calendar.showOtherMonthDays(false)

📅 Weekday Label Modes

You can choose how weekdays appear:

enum class WeekdayLabelMode {
    ONE_LETTER,      // S M T W T F S
    THREE_LETTERS,   // Sun Mon Tue ...
    FULL             // Sunday Monday ...
}


Usage:

app:weekdayLabelMode="three_letters"

🎛 Customization Examples

Highlight Today

app:todayTextColor="@color/red"
app:todayBackground="@drawable/bg_today"

Rounded Selected Day

app:selectedDayBackground="@drawable/bg_selected_circle"

iOS-style softer weekday labels

app:weekdayTextColor="@color/lightGray"
app:weekdayTextStyle="@style/TextStyleWeekday"

📌 Events & Callbacks

calendar.setOnDateSelectedListener { date ->
    Log.d("Calendar", "User selected: $date")
}

💡 Roadmap

 Event indicators

 Range selection

 Animations between months

 Dark-mode dynamic theming

 Material 3 support

📄 License

MIT License
Copyright (c) 2025
