package pan.alexander.tordnscrypt.utils.modulesStatus;

/*
    This file is part of InviZible Pro.

    InviZible Pro is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    InviZible Pro is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with InviZible Pro.  If not, see <http://www.gnu.org/licenses/>.

    Copyright 2019 by Garmatin Oleksandr invizible.soft@gmail.com
*/

import android.content.Context;
import android.content.Intent;

import java.util.concurrent.TimeUnit;

import pan.alexander.tordnscrypt.utils.enums.ModuleState;

import static pan.alexander.tordnscrypt.TopFragment.TOP_BROADCAST;
import static pan.alexander.tordnscrypt.utils.enums.ModuleState.RESTARTED;
import static pan.alexander.tordnscrypt.utils.enums.ModuleState.RESTARTING;
import static pan.alexander.tordnscrypt.utils.enums.ModuleState.RUNNING;
import static pan.alexander.tordnscrypt.utils.enums.ModuleState.STARTING;
import static pan.alexander.tordnscrypt.utils.enums.ModuleState.STOPPED;

public final class ModulesStatus {

    private volatile ModuleState dnsCryptState = STOPPED;
    private volatile ModuleState torState = STOPPED;
    private volatile ModuleState itpdState = STOPPED;

    private boolean useModulesWithRoot;

    private static volatile ModulesStatus modulesStatus;

    private ModulesStatus() {
    }

    public static ModulesStatus getInstance() {
        if (modulesStatus == null) {
            synchronized (ModulesStatus.class) {
                if (modulesStatus == null) {
                    modulesStatus = new ModulesStatus();
                }
            }
        }
        return modulesStatus;
    }

    public void refreshViews(Context context) {
        if (isUseModulesWithRoot()) {
            Intent intent = new Intent(TOP_BROADCAST);
            context.sendBroadcast(intent);
        } else {
            ModulesVersions.getInstance().refreshVersions(context);
        }

    }

    public void setUseModulesWithRoot(boolean useModulesWithRoot) {
        this.useModulesWithRoot = useModulesWithRoot;

    }

    public ModuleState getDnsCryptState() {
        return dnsCryptState;
    }

    public ModuleState getTorState() {
        return torState;
    }

    public ModuleState getItpdState() {
        return itpdState;
    }

    public void setDnsCryptState(ModuleState dnsCryptState) {
        this.dnsCryptState = dnsCryptState;
    }

    public void setTorState(ModuleState torState) {
        this.torState = torState;
    }

    public void setItpdState(ModuleState itpdState) {
        this.itpdState = itpdState;
    }

    public void setDnsCryptRestarting(final int timeSec) {

        setDnsCryptState(RESTARTING);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(timeSec);
                    setDnsCryptState(RESTARTED);
                } catch (InterruptedException ignored){}

            }
        }).start();
    }

    public void setTorRestarting(final int timeSec) {
        setTorState(RESTARTING);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(timeSec);
                    if (getTorState() != RUNNING) {
                        setTorState(STARTING);
                    }
                } catch (InterruptedException ignored){}
            }
        }).start();
    }

    public void setItpdRestarting(final int timeSec) {
        setItpdState(RESTARTING);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(timeSec);
                    setItpdState(RESTARTED);
                } catch (InterruptedException ignored){}
            }
        }).start();
    }

    public boolean isUseModulesWithRoot() {
        return useModulesWithRoot;
    }
}
