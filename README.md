# MultiProgressBar

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.geniusrus/multiprogressbar/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.geniusrus/multiprogressbar)
[![codebeat badge](https://codebeat.co/badges/68c23e0f-9f62-4443-a05b-490baaff8f9d)](https://codebeat.co/projects/github-com-geniusrus-multiprogressbar-master)

<img src="https://media.giphy.com/media/2wh244C33YbFTPr6zx/giphy.gif" width="278" height="480"/>

## Short description

This library makes it possible to display a progress bar, as in Instagram Stories, without much effort.

View support state recovery using `onSaveInstanceState`

## Details

These attributes (`app` in your layout) allow you to fine-tune the behavior of the progress bar:

1. `lineColor` (color) default: `Color.GRAY`
1. `progressColor` (color) default: `Color.WHITE`
1. `progressSteps` (integer) default: `1`
1. `progressPadding` (dimension) default: `8dp`
1. `progressWidth` (dimension) default: `10F`
1. `progressPercents` (integer) default: `100`
1. `isNeedRestoreProgress` (boolean) default: `false`
1. `singleDisplayedTime` (float) default: `1F`

Also the following api allows you to control the display of progress:

1. `start()` - starts / resumes showing progress
1. `pause()` - pauses showing progress. The scale stops at the current position
1. `previous()` - move to the beginning of the previous block of progress. If progress is started, the show will start immediately. Otherwise, it will stop at the beginning of the block.
1. `next()` - move to the beginning of the next block of progress. If progress is started, the show will start immediately. Otherwise, it will stop at the beginning of the block.
1. `clear()` - clearing the current state of progress, resetting the scale to the very beginning
1. `setListener(listener: ProgressStepChangeListener)` - sets the listener `ProgressStepChangeListener` to switch progress blocks
1. `setProgressPercents(progress: Int` - sets the number of percent for calculating the progress scale
1. `setProgressStepsCount(stepsCount: Int)` - sets the total number of progress steps
1. `setSingleDisplayTime(singleDisplayedTime: Float)` - sets the display time of one cell. Can be used in runtime

``` kotlin
interface ProgressStepChangeListener {
    fun onProgressStepChange(newStep: Int)
}
```

## Usage

Artifact is publishing to Maven Central. You can add this repository to your project with:
```gradle
repositories {
    mavenCentral()
}
```

Add to your .gradle file:
```gradle
implementation "io.github.geniusrus:multiprogressbar:$latest_version"
```

## Sample

The sample is on `app` module

## Developers

1. Viktor Likhanov

Yandex: [Gen1usRUS@yandex.ru](mailto:Gen1usRUS@yandex.ru)

## License
```
Apache v2.0 License

Copyright (c) 2019 Viktor Likhanov
