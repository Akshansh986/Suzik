/*
 * Copyright (C) 2014 Saravan Pantham
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blackMonster.suzik.musicPlayer.BroadcastReceivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blackMonster.suzik.musicPlayer.UIcontroller;
import com.blackMonster.suzik.ui.UiBroadcasts;

public class StopServiceBroadcastReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		//Stop the service.
      UIcontroller uIcontroller=  UIcontroller.getInstance(context);
        uIcontroller.stopPlayer();

        uIcontroller.senduibtnsetbroadcast();
        UiBroadcasts.broadcastMusicDataChanged(context);

	}

}