Feature: MusicPlayerTests

Scenario: Test all public methods when no tracks
    Given a MusicPlayer instance
    Then test public methods when given 0 tracks

Scenario: Test all public methods when one track is added
    Given a MusicPlayer instance
    Given 1 good track info added
    When no network
    Then test public methods when given 1 tracks

Scenario: Test all public methods when two track is added
    Given a MusicPlayer instance
    Given 2 good track info added
    When no network
    Then test public methods when given 2 tracks
