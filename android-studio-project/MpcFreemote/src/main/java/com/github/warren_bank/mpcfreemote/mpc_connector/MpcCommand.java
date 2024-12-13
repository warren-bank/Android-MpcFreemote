package com.github.warren_bank.mpcfreemote.mpc_connector;

/*
 * based on:
 *   https://github.com/eeeeeric/mpc-hc-api/blob/0.1.0/src/main/java/com/eeeeeric/mpc/hc/api/WMCommand.java
 */

import com.github.warren_bank.mpcfreemote.R;
import com.github.warren_bank.mpcfreemote.MainApp;

import com.eeeeeric.mpc.hc.api.WMCommand;

/**
 * These are commands that MPC-HC recognizes.
 */
public enum MpcCommand {
  SET_VOLUME(
    -2, R.string.SET_VOLUME // "Set Volume"
  ),
  SEEK(
    -1, R.string.SEEK // "Seek"
  ),
  QUICK_OPEN_FILE(
    969, R.string.QUICK_OPEN_FILE // "Quick Open File"
  ),
  OPEN_FILE(
    800, R.string.OPEN_FILE // "Open File"
  ),
  OPEN_DVD_BD(
    801, R.string.OPEN_DVD_BD // "Open DVD/BD"
  ),
  OPEN_DEVICE(
    802, R.string.OPEN_DEVICE // "Open Device"
  ),
  REOPEN_FILE(
    976, R.string.REOPEN_FILE // "Reopen File"
  ),
  MOVE_TO_RECYCLE_BIN(
    24044, R.string.MOVE_TO_RECYCLE_BIN // "Move to Recycle Bin"
  ),
  SAVE_A_COPY(
    805, R.string.SAVE_A_COPY // "Save a Copy"
  ),
  SAVE_IMAGE(
    806, R.string.SAVE_IMAGE // "Save Image"
  ),
  SAVE_IMAGE_AUTO(
    807, R.string.SAVE_IMAGE_AUTO // "Save Image (auto)"
  ),
  SAVE_THUMBNAILS(
    808, R.string.SAVE_THUMBNAILS // "Save thumbnails"
  ),
  LOAD_SUBTITLE(
    809, R.string.LOAD_SUBTITLE // "Load Subtitle"
  ),
  SAVE_SUBTITLE(
    810, R.string.SAVE_SUBTITLE // "Save Subtitle"
  ),
  CLOSE(
    804, R.string.CLOSE // "Close"
  ),
  PROPERTIES(
    814, R.string.PROPERTIES // "Properties"
  ),
  EXIT(
    816, R.string.EXIT // "Exit"
  ),
  PLAY_PAUSE(
    889, R.string.PLAY_PAUSE // "Play/Pause"
  ),
  PLAY(
    887, R.string.PLAY // "Play"
  ),
  PAUSE(
    888, R.string.PAUSE // "Pause"
  ),
  STOP(
    890, R.string.STOP // "Stop"
  ),
  FRAMESTEP(
    891, R.string.FRAMESTEP // "Framestep"
  ),
  FRAMESTEP_BACK(
    892, R.string.FRAMESTEP_BACK // "Framestep back"
  ),
  GO_TO(
    893, R.string.GO_TO // "Go To"
  ),
  INCREASE_RATE(
    895, R.string.INCREASE_RATE // "Increase Rate"
  ),
  DECREASE_RATE(
    894, R.string.DECREASE_RATE // "Decrease Rate"
  ),
  RESET_RATE(
    896, R.string.RESET_RATE // "Reset Rate"
  ),
  AUDIO_DELAY_PLUS_10_MS(
    905, R.string.AUDIO_DELAY_PLUS_10_MS // "Audio Delay +10 ms"
  ),
  AUDIO_DELAY_MINUS_10_MS(
    906, R.string.AUDIO_DELAY_MINUS_10_MS // "Audio Delay -10 ms"
  ),
  JUMP_FORWARD_SMALL(
    900, R.string.JUMP_FORWARD_SMALL // "Jump Forward (small)"
  ),
  JUMP_BACKWARD_SMALL(
    899, R.string.JUMP_BACKWARD_SMALL // "Jump Backward (small)"
  ),
  JUMP_FORWARD_MEDIUM(
    902, R.string.JUMP_FORWARD_MEDIUM // "Jump Forward (medium)"
  ),
  JUMP_BACKWARD_MEDIUM(
    901, R.string.JUMP_BACKWARD_MEDIUM // "Jump Backward (medium)"
  ),
  JUMP_FORWARD_LARGE(
    904, R.string.JUMP_FORWARD_LARGE // "Jump Forward (large)"
  ),
  JUMP_BACKWARD_LARGE(
    903, R.string.JUMP_BACKWARD_LARGE // "Jump Backward (large)"
  ),
  JUMP_FORWARD_KEYFRAME(
    898, R.string.JUMP_FORWARD_KEYFRAME // "Jump Forward (keyframe)"
  ),
  JUMP_BACKWARD_KEYFRAME(
    897, R.string.JUMP_BACKWARD_KEYFRAME // "Jump Backward (keyframe)"
  ),
  JUMP_TO_BEGINNING(
    996, R.string.JUMP_TO_BEGINNING // "Jump to Beginning"
  ),
  NEXT(
    922, R.string.NEXT // "Next"
  ),
  PREVIOUS(
    921, R.string.PREVIOUS // "Previous"
  ),
  NEXT_FILE(
    920, R.string.NEXT_FILE // "Next File"
  ),
  PREVIOUS_FILE(
    919, R.string.PREVIOUS_FILE // "Previous File"
  ),
  TUNER_SCAN(
    974, R.string.TUNER_SCAN // "Tuner scan"
  ),
  QUICK_ADD_FAVORITE(
    975, R.string.QUICK_ADD_FAVORITE // "Quick add favorite"
  ),
  TOGGLE_CAPTION_AND_MENU(
    817, R.string.TOGGLE_CAPTION_AND_MENU // "Toggle Caption and Menu"
  ),
  TOGGLE_SEEKER(
    818, R.string.TOGGLE_SEEKER // "Toggle Seeker"
  ),
  TOGGLE_CONTROLS(
    819, R.string.TOGGLE_CONTROLS // "Toggle Controls"
  ),
  TOGGLE_INFORMATION(
    820, R.string.TOGGLE_INFORMATION // "Toggle Information"
  ),
  TOGGLE_STATISTICS(
    821, R.string.TOGGLE_STATISTICS // "Toggle Statistics"
  ),
  TOGGLE_STATUS(
    822, R.string.TOGGLE_STATUS // "Toggle Status"
  ),
  TOGGLE_SUBRESYNC_BAR(
    823, R.string.TOGGLE_SUBRESYNC_BAR // "Toggle Subresync Bar"
  ),
  TOGGLE_PLAYLIST_BAR(
    824, R.string.TOGGLE_PLAYLIST_BAR // "Toggle Playlist Bar"
  ),
  TOGGLE_CAPTURE_BAR(
    825, R.string.TOGGLE_CAPTURE_BAR // "Toggle Capture Bar"
  ),
  TOGGLE_NAVIGATION_BAR(
    33415, R.string.TOGGLE_NAVIGATION_BAR // "Toggle Navigation Bar"
  ),
  TOGGLE_DEBUG_SHADERS(
    826, R.string.TOGGLE_DEBUG_SHADERS // "Toggle Debug Shaders"
  ),
  VIEW_MINIMAL(
    827, R.string.VIEW_MINIMAL // "View Minimal"
  ),
  VIEW_COMPACT(
    828, R.string.VIEW_COMPACT // "View Compact"
  ),
  VIEW_NORMAL(
    829, R.string.VIEW_NORMAL // "View Normal"
  ),
  FULLSCREEN(
    830, R.string.FULLSCREEN // "Fullscreen"
  ),
  FULLSCREEN_WITHOUT_RES_CHANGE(
    831, R.string.FULLSCREEN_WITHOUT_RES_CHANGE // "Fullscreen (w/o res.change)"
  ),
  ZOOM_50(
    832, R.string.ZOOM_50 // "Zoom 50%"
  ),
  ZOOM_100(
    833, R.string.ZOOM_100 // "Zoom 100%"
  ),
  ZOOM_200(
    834, R.string.ZOOM_200 // "Zoom 200%"
  ),
  ZOOM_AUTO_FIT(
    968, R.string.ZOOM_AUTO_FIT // "Zoom Auto Fit"
  ),
  ZOOM_AUTO_FIT_LARGER_ONLY(
    4900, R.string.ZOOM_AUTO_FIT_LARGER_ONLY // "Zoom Auto Fit (Larger Only)"
  ),
  NEXT_AR_PRESET(
    859, R.string.NEXT_AR_PRESET // "Next AR Preset"
  ),
  VIDFRM_HALF(
    835, R.string.VIDFRM_HALF // "VidFrm Half"
  ),
  VIDFRM_NORMAL(
    836, R.string.VIDFRM_NORMAL // "VidFrm Normal"
  ),
  VIDFRM_DOUBLE(
    837, R.string.VIDFRM_DOUBLE // "VidFrm Double"
  ),
  VIDFRM_STRETCH(
    838, R.string.VIDFRM_STRETCH // "VidFrm Stretch"
  ),
  VIDFRM_INSIDE(
    839, R.string.VIDFRM_INSIDE // "VidFrm Inside"
  ),
  VIDFRM_ZOOM_1(
    841, R.string.VIDFRM_ZOOM_1 // "VidFrm Zoom 1"
  ),
  VIDFRM_ZOOM_2(
    842, R.string.VIDFRM_ZOOM_2 // "VidFrm Zoom 2"
  ),
  VIDFRM_OUTSIDE(
    840, R.string.VIDFRM_OUTSIDE // "VidFrm Outside"
  ),
  VIDFRM_SWITCH_ZOOM(
    843, R.string.VIDFRM_SWITCH_ZOOM // "VidFrm Switch Zoom"
  ),
  ALWAYS_ON_TOP(
    884, R.string.ALWAYS_ON_TOP // "Always On Top"
  ),
  PNS_RESET(
    861, R.string.PNS_RESET // "PnS Reset"
  ),
  PNS_INC_SIZE(
    862, R.string.PNS_INC_SIZE // "PnS Inc Size"
  ),
  PNS_INC_WIDTH(
    864, R.string.PNS_INC_WIDTH // "PnS Inc Width"
  ),
  PNS_INC_HEIGHT(
    866, R.string.PNS_INC_HEIGHT // "PnS Inc Height"
  ),
  PNS_DEC_SIZE(
    863, R.string.PNS_DEC_SIZE // "PnS Dec Size"
  ),
  PNS_DEC_WIDTH(
    865, R.string.PNS_DEC_WIDTH // "PnS Dec Width"
  ),
  PNS_DEC_HEIGHT(
    867, R.string.PNS_DEC_HEIGHT // "PnS Dec Height"
  ),
  PNS_CENTER(
    876, R.string.PNS_CENTER // "PnS Center"
  ),
  PNS_LEFT(
    868, R.string.PNS_LEFT // "PnS Left"
  ),
  PNS_RIGHT(
    869, R.string.PNS_RIGHT // "PnS Right"
  ),
  PNS_UP(
    870, R.string.PNS_UP // "PnS Up"
  ),
  PNS_DOWN(
    871, R.string.PNS_DOWN // "PnS Down"
  ),
  PNS_UP_LEFT(
    872, R.string.PNS_UP_LEFT // "PnS Up/Left"
  ),
  PNS_UP_RIGHT(
    873, R.string.PNS_UP_RIGHT // "PnS Up/Right"
  ),
  PNS_DOWN_LEFT(
    874, R.string.PNS_DOWN_LEFT // "PnS Down/Left"
  ),
  PNS_DOWN_RIGHT(
    875, R.string.PNS_DOWN_RIGHT // "PnS Down/Right"
  ),
  PNS_ROTATE_X_PLUS(
    877, R.string.PNS_ROTATE_X_PLUS // "PnS Rotate X+"
  ),
  PNS_ROTATE_X_MINUS(
    878, R.string.PNS_ROTATE_X_MINUS // "PnS Rotate X-"
  ),
  PNS_ROTATE_Y_PLUS(
    879, R.string.PNS_ROTATE_Y_PLUS // "PnS Rotate Y+"
  ),
  PNS_ROTATE_Y_MINUS(
    880, R.string.PNS_ROTATE_Y_MINUS // "PnS Rotate Y-"
  ),
  PNS_ROTATE_Z_PLUS(
    881, R.string.PNS_ROTATE_Z_PLUS // "PnS Rotate Z+"
  ),
  PNS_ROTATE_Z_MINUS(
    882, R.string.PNS_ROTATE_Z_MINUS // "PnS Rotate Z-"
  ),
  VOLUME_UP(
    907, R.string.VOLUME_UP // "Volume Up"
  ),
  VOLUME_DOWN(
    908, R.string.VOLUME_DOWN // "Volume Down"
  ),
  VOLUME_MUTE(
    909, R.string.VOLUME_MUTE // "Volume Mute"
  ),
  VOLUME_BOOST_INCREASE(
    970, R.string.VOLUME_BOOST_INCREASE // "Volume boost increase"
  ),
  VOLUME_BOOST_DECREASE(
    971, R.string.VOLUME_BOOST_DECREASE // "Volume boost decrease"
  ),
  VOLUME_BOOST_MIN(
    972, R.string.VOLUME_BOOST_MIN // "Volume boost Min"
  ),
  VOLUME_BOOST_MAX(
    973, R.string.VOLUME_BOOST_MAX // "Volume boost Max"
  ),
  TOGGLE_CUSTOM_CHANNEL_MAPPING(
    993, R.string.TOGGLE_CUSTOM_CHANNEL_MAPPING // "Toggle custom channel mapping"
  ),
  TOGGLE_NORMALIZATION(
    994, R.string.TOGGLE_NORMALIZATION // "Toggle normalization"
  ),
  TOGGLE_REGAIN_VOLUME(
    995, R.string.TOGGLE_REGAIN_VOLUME // "Toggle regain volume"
  ),
  BRIGHTNESS_INCREASE(
    984, R.string.BRIGHTNESS_INCREASE // "Brightness increase"
  ),
  BRIGHTNESS_DECREASE(
    985, R.string.BRIGHTNESS_DECREASE // "Brightness decrease"
  ),
  CONTRAST_INCREASE(
    986, R.string.CONTRAST_INCREASE // "Contrast increase"
  ),
  CONTRAST_DECREASE(
    987, R.string.CONTRAST_DECREASE // "Contrast decrease"
  ),
  HUE_INCREASE(
    988, R.string.HUE_INCREASE // "Hue increase"
  ),
  HUE_DECREASE(
    989, R.string.HUE_DECREASE // "Hue decrease"
  ),
  SATURATION_INCREASE(
    990, R.string.SATURATION_INCREASE // "Saturation increase"
  ),
  SATURATION_DECREASE(
    991, R.string.SATURATION_DECREASE // "Saturation decrease"
  ),
  RESET_COLOR_SETTINGS(
    992, R.string.RESET_COLOR_SETTINGS // "Reset color settings"
  ),
  DVD_TITLE_MENU(
    923, R.string.DVD_TITLE_MENU // "DVD Title Menu"
  ),
  DVD_ROOT_MENU(
    924, R.string.DVD_ROOT_MENU // "DVD Root Menu"
  ),
  DVD_SUBTITLE_MENU(
    925, R.string.DVD_SUBTITLE_MENU // "DVD Subtitle Menu"
  ),
  DVD_AUDIO_MENU(
    926, R.string.DVD_AUDIO_MENU // "DVD Audio Menu"
  ),
  DVD_ANGLE_MENU(
    927, R.string.DVD_ANGLE_MENU // "DVD Angle Menu"
  ),
  DVD_CHAPTER_MENU(
    928, R.string.DVD_CHAPTER_MENU // "DVD Chapter Menu"
  ),
  DVD_MENU_LEFT(
    929, R.string.DVD_MENU_LEFT // "DVD Menu Left"
  ),
  DVD_MENU_RIGHT(
    930, R.string.DVD_MENU_RIGHT // "DVD Menu Right"
  ),
  DVD_MENU_UP(
    931, R.string.DVD_MENU_UP // "DVD Menu Up"
  ),
  DVD_MENU_DOWN(
    932, R.string.DVD_MENU_DOWN // "DVD Menu Down"
  ),
  DVD_MENU_ACTIVATE(
    933, R.string.DVD_MENU_ACTIVATE // "DVD Menu Activate"
  ),
  DVD_MENU_BACK(
    934, R.string.DVD_MENU_BACK // "DVD Menu Back"
  ),
  DVD_MENU_LEAVE(
    935, R.string.DVD_MENU_LEAVE // "DVD Menu Leave"
  ),
  BOSS_KEY(
    944, R.string.BOSS_KEY // "Boss key"
  ),
  PLAYER_MENU_SHORT(
    949, R.string.PLAYER_MENU_SHORT // "Player Menu (short)"
  ),
  PLAYER_MENU_LONG(
    950, R.string.PLAYER_MENU_LONG // "Player Menu (long)"
  ),
  FILTERS_MENU(
    951, R.string.FILTERS_MENU // "Filters Menu"
  ),
  OPTIONS(
    815, R.string.OPTIONS // "Options"
  ),
  NEXT_AUDIO(
    952, R.string.NEXT_AUDIO // "Next Audio"
  ),
  PREV_AUDIO(
    953, R.string.PREV_AUDIO // "Prev Audio"
  ),
  NEXT_SUBTITLE(
    954, R.string.NEXT_SUBTITLE // "Next Subtitle"
  ),
  PREV_SUBTITLE(
    955, R.string.PREV_SUBTITLE // "Prev Subtitle"
  ),
  ON_OFF_SUBTITLE(
    956, R.string.ON_OFF_SUBTITLE // "On/Off Subtitle"
  ),
  RELOAD_SUBTITLES(
    2302, R.string.RELOAD_SUBTITLES // "Reload Subtitles"
  ),
  DOWNLOAD_SUBTITLES(
    812, R.string.DOWNLOAD_SUBTITLES // "Download subtitles"
  ),
  NEXT_AUDIO_OGM(
    957, R.string.NEXT_AUDIO_OGM // "Next Audio (OGM)"
  ),
  PREV_AUDIO_OGM(
    958, R.string.PREV_AUDIO_OGM // "Prev Audio (OGM)"
  ),
  NEXT_SUBTITLE_OGM(
    959, R.string.NEXT_SUBTITLE_OGM // "Next Subtitle (OGM)"
  ),
  PREV_SUBTITLE_OGM(
    960, R.string.PREV_SUBTITLE_OGM // "Prev Subtitle (OGM)"
  ),
  NEXT_ANGLE_DVD(
    961, R.string.NEXT_ANGLE_DVD // "Next Angle (DVD)"
  ),
  PREV_ANGLE_DVD(
    962, R.string.PREV_ANGLE_DVD // "Prev Angle (DVD)"
  ),
  NEXT_AUDIO_DVD(
    963, R.string.NEXT_AUDIO_DVD // "Next Audio (DVD)"
  ),
  PREV_AUDIO_DVD(
    964, R.string.PREV_AUDIO_DVD // "Prev Audio (DVD)"
  ),
  NEXT_SUBTITLE_DVD(
    965, R.string.NEXT_SUBTITLE_DVD // "Next Subtitle (DVD)"
  ),
  PREV_SUBTITLE_DVD(
    966, R.string.PREV_SUBTITLE_DVD // "Prev Subtitle (DVD)"
  ),
  ON_OFF_SUBTITLE_DVD(
    967, R.string.ON_OFF_SUBTITLE_DVD // "On/Off Subtitle (DVD)"
  ),
  TEARING_TEST(
    32769, R.string.TEARING_TEST // "Tearing Test"
  ),
  REMAINING_TIME(
    32778, R.string.REMAINING_TIME // "Remaining Time"
  ),
  NEXT_SHADER_PRESET(
    57382, R.string.NEXT_SHADER_PRESET // "Next Shader Preset"
  ),
  PREV_SHADER_PRESET(
    57384, R.string.PREV_SHADER_PRESET // "Prev Shader Preset"
  ),
  TOGGLE_DIRECT3D_FULLSCREEN(
    32779, R.string.TOGGLE_DIRECT3D_FULLSCREEN // "Toggle Direct3D fullscreen"
  ),
  GOTO_PREV_SUBTITLE(
    32780, R.string.GOTO_PREV_SUBTITLE // "Goto Prev Subtitle"
  ),
  GOTO_NEXT_SUBTITLE(
    32781, R.string.GOTO_NEXT_SUBTITLE // "Goto Next Subtitle"
  ),
  SHIFT_SUBTITLE_LEFT(
    32782, R.string.SHIFT_SUBTITLE_LEFT // "Shift Subtitle Left"
  ),
  SHIFT_SUBTITLE_RIGHT(
    32783, R.string.SHIFT_SUBTITLE_RIGHT // "Shift Subtitle Right"
  ),
  DISPLAY_STATS(
    32784, R.string.DISPLAY_STATS // "Display Stats"
  ),
  RESET_DISPLAY_STATS(
    33405, R.string.RESET_DISPLAY_STATS // "Reset Display Stats"
  ),
  VSYNC(
    33243, R.string.VSYNC // "VSync"
  ),
  ENABLE_FRAME_TIME_CORRECTION(
    33265, R.string.ENABLE_FRAME_TIME_CORRECTION // "Enable Frame Time Correction"
  ),
  ACCURATE_VSYNC(
    33260, R.string.ACCURATE_VSYNC // "Accurate VSync"
  ),
  DECREASE_VSYNC_OFFSET(
    33246, R.string.DECREASE_VSYNC_OFFSET // "Decrease VSync Offset"
  ),
  INCREASE_VSYNC_OFFSET(
    33247, R.string.INCREASE_VSYNC_OFFSET // "Increase VSync Offset"
  ),
  SUBTITLE_DELAY_MINUS(
    24000, R.string.SUBTITLE_DELAY_MINUS // "Subtitle Delay -"
  ),
  SUBTITLE_DELAY_PLUS(
    24001, R.string.SUBTITLE_DELAY_PLUS // "Subtitle Delay +"
  ),
  AFTER_PLAYBACK_EXIT(
    912, R.string.AFTER_PLAYBACK_EXIT // "After Playback: Exit"
  ),
  AFTER_PLAYBACK_STAND_BY(
    913, R.string.AFTER_PLAYBACK_STAND_BY // "After Playback: Stand By"
  ),
  AFTER_PLAYBACK_HIBERNATE(
    914, R.string.AFTER_PLAYBACK_HIBERNATE // "After Playback: Hibernate"
  ),
  AFTER_PLAYBACK_SHUTDOWN(
    915, R.string.AFTER_PLAYBACK_SHUTDOWN // "After Playback: Shutdown"
  ),
  AFTER_PLAYBACK_LOG_OFF(
    916, R.string.AFTER_PLAYBACK_LOG_OFF // "After Playback: Log Off"
  ),
  AFTER_PLAYBACK_LOCK(
    917, R.string.AFTER_PLAYBACK_LOCK // "After Playback: Lock"
  ),
  AFTER_PLAYBACK_TURN_OFF_THE_MONITOR(
    918, R.string.AFTER_PLAYBACK_TURN_OFF_THE_MONITOR // "After Playback: Turn off the monitor"
  ),
  AFTER_PLAYBACK_PLAY_NEXT_FILE_IN_THE_FOLDER(
    947, R.string.AFTER_PLAYBACK_PLAY_NEXT_FILE_IN_THE_FOLDER // "After Playback: Play next file in the folder"
  ),
  TOGGLE_EDL_WINDOW(
    846, R.string.TOGGLE_EDL_WINDOW // "Toggle EDL window"
  ),
  EDL_SET_IN(
    847, R.string.EDL_SET_IN // "EDL set In"
  ),
  EDL_SET_OUT(
    848, R.string.EDL_SET_OUT // "EDL set Out"
  ),
  EDL_NEW_CLIP(
    849, R.string.EDL_NEW_CLIP // "EDL new clip"
  ),
  EDL_SAVE(
    860, R.string.EDL_SAVE // "EDL save"
  );

  private int value;
  private int commandNameResourceId;

  /**
   * Create a new instance.
   *
   * @param commandName
   *        A human friendly string of what the command does
   * @param value
   *        The integer command code
   */
  MpcCommand(int value, int commandNameResourceId) {
    this.value = value;
    this.commandNameResourceId = commandNameResourceId;
  }

  /**
   * Returns the integer command code.
   *
   * @return the integer command code.
   */
  public int getValue() {
    return value;
  }

  /**
   * Returns the resource id for a human friendly string of what the command does
   *
   * @return the resource id for a human friendly string of what the command does
   */
  public int getCommandNameResourceId() {
    return commandNameResourceId;
  }

  /**
   * Returns a human friendly string of what the command does
   *
   * @return a human friendly string of what the command does
   */
  public String getCommandName() {
    return getString(commandNameResourceId);
  }

  @Override
  public String toString() {
    return getCommandName();
  }

  public WMCommand getWMCommand() {
    try {
      String commonName = name();
      WMCommand command = WMCommand.valueOf(commonName);
      return command;
    }
    catch(Exception e) {}
    return null;
  }

  private static String getString(int resourceId) {
    return MainApp.getInstance().getString(resourceId);
  }
}
